package com.uw.hcde.fizzlab.trace.ui.drawing;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SendCallback;
import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.dataContainer.TraceDataContainerSender;
import com.uw.hcde.fizzlab.trace.database.ParseAnnotation;
import com.uw.hcde.fizzlab.trace.database.ParseConstant;
import com.uw.hcde.fizzlab.trace.database.ParseDataFactory;
import com.uw.hcde.fizzlab.trace.database.callback.ParseAddFriendCallback;
import com.uw.hcde.fizzlab.trace.database.callback.ParseRetrieveFriendsCallback;
import com.uw.hcde.fizzlab.trace.database.callback.ParseSendDrawingCallback;
import com.uw.hcde.fizzlab.trace.ui.BaseActivity;
import com.uw.hcde.fizzlab.trace.ui.FriendListAdapter;
import com.uw.hcde.fizzlab.trace.utility.TraceUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import info.hoang8f.widget.FButton;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * @author tianchi
 */

public class SelectFriendFragment extends Fragment implements ParseRetrieveFriendsCallback, ParseAddFriendCallback, ParseSendDrawingCallback {

    private static final String TAG = "SelectFriendFragment";
    private ListView mFriendsList;
    private FriendListAdapter mAdapter;
    private View mButtonSend;
    private FButton mButtonAddFriend;
    private Context mContext;
    private ProgressDialog mProgressDialog;
    private Set<ParseUser> mSelectedUsers;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_friend, container, false);
        ((BaseActivity) getActivity()).setNavigationTitle(R.string.draw_step_3);

        mContext = getActivity();
        mButtonSend = view.findViewById(R.id.button_next);
        mButtonAddFriend = (FButton) view.findViewById(R.id.add_friend);
        mButtonAddFriend.setButtonColor(getResources().getColor(R.color.rose_light));
        mButtonAddFriend.setShadowColor(getResources().getColor(R.color.rose_dark));
        mAdapter = null;
        mFriendsList = (ListView) view.findViewById(R.id.friend_list);
        setupListener();

        ParseDataFactory.retrieveFriends(ParseUser.getCurrentUser(), this);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.progress_sending));
        mProgressDialog.setCanceledOnTouchOutside(false);
        return view;
    }

    /**
     * Helper function to add button listeners
     */
    private void setupListener() {
        mButtonAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MaterialDialog dialog = new MaterialDialog(mContext);
                dialog.setTitle(R.string.add_friends);

                // Set up dialog view
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.dialog_add_friend, null);
                final EditText inputName = (EditText) view.findViewById(R.id.input_username);

                // Build dialog
                dialog.setContentView(view);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setPositiveButton(R.string.add, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = inputName.getText().toString().trim();

                        if (name.length() == 0) {
                            TraceUtil.showToast(mContext, getString(R.string.please_enter_something));
                            return;
                        } else {
                            ParseDataFactory.addFriend(ParseUser.getCurrentUser(), name, SelectFriendFragment.this);
                        }
                        dialog.dismiss();
                    }
                });

                dialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter == null) {
                    TraceUtil.showToast(mContext, getString(R.string.please_select_friends));
                    return;
                }

                mSelectedUsers = mAdapter.getSelectedUsers();
                if (mSelectedUsers.isEmpty()) {
                    TraceUtil.showToast(mContext, getString(R.string.please_select_friends));
                    return;
                }


                final MaterialDialog dialog = new MaterialDialog(getActivity());
                dialog.setTitle(R.string.send);

                // Set up dialog view
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.dialog_send, null);
                final EditText inputDescription = (EditText) view.findViewById(R.id.input_description);

                // Build dialog
                dialog.setContentView(view);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setPositiveButton(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Get description
                        String description = inputDescription.getText().toString().trim();
                        if (description.length() == 0) {
                            TraceUtil.showToast(getActivity(), getString(R.string.toast_enter_description));
                            return;
                        } else {
                            TraceDataContainerSender.description = description;
                        }

                        // Send data
                        sendData();
                        dialog.dismiss();
                    }
                });

                dialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    public void parseRetrieveFriendsCallback(int returnCode, List<ParseUser> friends) {
        if (returnCode == ParseConstant.SUCCESS && friends.size() > 0) {
            Collections.sort(friends, new Comparator<ParseUser>() {
                @Override
                public int compare(ParseUser lhs, ParseUser rhs) {
                    String s1 = lhs.getString(ParseConstant.KEY_FULL_NAME).toLowerCase();
                    String s2 = rhs.getString(ParseConstant.KEY_FULL_NAME).toLowerCase();
                    return s1.compareTo(s2);
                }
            });
            mAdapter = new FriendListAdapter(mContext, R.layout.list_item_friend, friends, FriendListAdapter.ENABLE_CHECK_BOX);
            mFriendsList.setAdapter(mAdapter);
        }
    }

    @Override
    public void parseAddFriendCallback(int returnCode) {
        if (returnCode == ParseConstant.SUCCESS) {
            ParseDataFactory.retrieveFriends(ParseUser.getCurrentUser(), this);
            TraceUtil.showToast(mContext, getString(R.string.toast_success));
        } else {
            TraceUtil.showToast(mContext, getString(R.string.invalid_trace_user));
        }
    }

    /**
     * Sends all data to parse database and sets up progress dialog
     * Send annotation -> Send drawing
     */
    private void sendData() {
        mProgressDialog.show();
        ParseDataFactory.sendAnnotation(TraceDataContainerSender.tracePoints, this);
    }

    @Override
    public void sendAnnotationCallback(int returnCode, List<ParseAnnotation> annotations) {
        if (returnCode == ParseConstant.SUCCESS) {
            List<ParseUser> users = new ArrayList<ParseUser>(mSelectedUsers);
            ParseDataFactory.sendDrawing(TraceDataContainerSender.description, users, TraceDataContainerSender.tracePoints, annotations, this);
        } else {
            mProgressDialog.dismiss();
            TraceUtil.showToast(getActivity(), getString(R.string.toast_network_error));
        }
    }

    @Override
    public void sendDrawingCallback(int returnCode) {
        if (returnCode == ParseConstant.SUCCESS) {
            mProgressDialog.dismiss();
            TraceUtil.showToast(getActivity(), getString(R.string.toast_success));

            // TODO: put notification code to parse data factory file

            // send notification to users
            ParseQuery pushQuery = ParseInstallation.getQuery();
            pushQuery.whereContainedIn("username", mSelectedUsers); // Set the target email

            // Send push notification to query by email
            ParsePush push = new ParsePush();
            push.setQuery(pushQuery);
            push.setMessage(ParseUser.getCurrentUser().getUsername() + " send you a new drawing: " + TraceDataContainerSender.description);
            push.sendInBackground(new SendCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d(TAG, "Success send welcome notification");
                    } else {
                        Log.e(TAG, "Failed send welcome notification");
                    }
                }
            });

            //
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getActivity().finish();
                }
            }, TraceUtil.TOAST_MESSAGE_TIME);

        } else {
            mProgressDialog.dismiss();
            TraceUtil.showToast(getActivity(), getString(R.string.toast_network_error));
        }
    }

}


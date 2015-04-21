package com.uw.hcde.fizzlab.trace.ui.profile;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Trace;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.Parse;
import com.parse.ParseUser;
import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.database.ParseConstant;
import com.uw.hcde.fizzlab.trace.database.ParseDataFactory;
import com.uw.hcde.fizzlab.trace.database.callback.ParseAddFriendCallback;
import com.uw.hcde.fizzlab.trace.database.callback.ParseRetrieveFriendsCallback;
import com.uw.hcde.fizzlab.trace.ui.FriendListAdapter;
import com.uw.hcde.fizzlab.trace.utility.TraceUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * @author tianchi
 */
public class FriendsFragment extends Fragment implements ParseRetrieveFriendsCallback, ParseAddFriendCallback {

    private static final String TAG = "FriendsFragment";
    private ListView mFriendsList;
    private FriendListAdapter mAdapter;
    private View mAddFriendButton;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_list, container, false);

        mContext = getActivity();
        mAdapter = null;
        mFriendsList = (ListView) view.findViewById(R.id.friend_list);
        mAddFriendButton = view.findViewById(R.id.add_friend);
        setupButtons();


        ParseDataFactory.retrieveFriends(ParseUser.getCurrentUser(), this);
        return view;
    }

    /**
     * Sets up buttons
     */
    private void setupButtons() {

        mAddFriendButton.setOnClickListener(new View.OnClickListener() {
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
                            ParseDataFactory.addFriend(ParseUser.getCurrentUser(), name, FriendsFragment.this);
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
            mAdapter = new FriendListAdapter(mContext, R.layout.list_item_friend, friends, FriendListAdapter.DISABLE_CHECK_BOX);
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
}

package com.uw.hcde.fizzlab.trace.ui.profile;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.database.ParseConstant;
import com.uw.hcde.fizzlab.trace.database.ParseDataFactory;
import com.uw.hcde.fizzlab.trace.database.callback.ParseAddFriendCallback;
import com.uw.hcde.fizzlab.trace.database.callback.ParseRetrieveFriendsCallback;
import com.uw.hcde.fizzlab.trace.utility.TraceUtil;

import java.util.ArrayList;
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
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

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
                            ParseDataFactory.addFriend(ParseUser.getCurrentUser(), "hi", FriendsFragment.this);
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


    private class FriendListAdapter extends ArrayAdapter<ParseUser> {

        // Use view holder pattern to improve performance
        private class ViewHolder {
            TextView mName;
            TextView mEmail;
        }

        public FriendListAdapter(Context context, int textViewResourceId, List<ParseUser> items) {
            super(context, textViewResourceId, items);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.list_item_friend, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.mName = (TextView) convertView.findViewById(R.id.friend_name);
                viewHolder.mEmail = (TextView) convertView.findViewById(R.id.friend_email);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            ParseUser item = getItem(position);
            if (position % 2 == 0) {
                convertView.setBackground(getResources().getDrawable(R.color.gray_light1));
            } else {
                convertView.setBackground(getResources().getDrawable(R.color.gray_light2));
            }

            if (item != null) {
                viewHolder.mName.setText(item.getString(ParseConstant.KEY_FULL_NAME));
                viewHolder.mEmail.setText(item.getEmail());
            }
            return convertView;
        }
    }

    @Override
    public void parseRetrieveFriendsCallback(int returnCode, List<ParseUser> friends) {
        if (returnCode == ParseConstant.SUCCESS && friends.size() > 0) {
            mAdapter = new FriendListAdapter(mContext, R.layout.list_item_friend, friends);
            mFriendsList.setAdapter(mAdapter);
        }
    }


    @Override
    public void parseAddFriendCallback(int returnCode, ParseUser friend) {
        if (returnCode == ParseConstant.SUCCESS) {
            if (mAdapter == null) {
                List<ParseUser> list = new ArrayList<ParseUser>();
                list.add(friend);
                mAdapter = new FriendListAdapter(mContext, R.layout.list_item_friend, list);
                mFriendsList.setAdapter(mAdapter);
            } else {
                mAdapter.add(friend);
            }
            TraceUtil.showToast(mContext, getString(R.string.toast_success));
        } else {
            TraceUtil.showToast(mContext, getString(R.string.invalid_trace_user));
        }
    }
}

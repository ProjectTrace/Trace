package com.uw.hcde.fizzlab.trace.ui.profile;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.database.ParseConstant;

import java.util.List;

/**
 * @author tianchi
 */
public class FriendsFragment extends Fragment {

    private ListView mFriendsList;
    private FriendListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        mAdapter = null;
        mFriendsList = (ListView) view.findViewById(R.id.friend_list);
        return view;
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
}

package com.uw.hcde.fizzlab.trace.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.parse.ParseUser;
import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.database.ParseConstant;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author tianchi
 */
public class FriendListAdapter extends ArrayAdapter<ParseUser> {
    public static final int ENABLE_CHECK_BOX = 0;
    public static final int DISABLE_CHECK_BOX = 1;

    private Context mContext;
    private boolean mCheckBoxEnabled;
    private Set mSelectedUsers;
    private List<ParseUser> items;


    // Use view holder pattern to improve performance
    private class ViewHolder {
        TextView mName;
        TextView mEmail;
        CheckBox mCheckBox;
    }

    public FriendListAdapter(Context context, int textViewResourceId, List<ParseUser> items, int enableCheckBox) {
        super(context, textViewResourceId, items);
        mContext = context;
        mCheckBoxEnabled = (enableCheckBox == ENABLE_CHECK_BOX) ? true : false;
        mSelectedUsers = new HashSet<ParseUser>();
        this.items = items;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.list_item_friend, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.mName = (TextView) convertView.findViewById(R.id.friend_name);
            viewHolder.mEmail = (TextView) convertView.findViewById(R.id.friend_email);


            if (mCheckBoxEnabled) {
                final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.check_box);
                checkBox.setVisibility(View.VISIBLE);
                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkBox.isChecked()) {
                            mSelectedUsers.add(items.get(position));
                        } else {
                            mSelectedUsers.remove(items.get(position));
                        }
                    }
                });
                viewHolder.mCheckBox = checkBox;
            }
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ParseUser item = getItem(position);
        if (position % 2 == 0) {
            convertView.setBackground(mContext.getResources().getDrawable(R.color.gray_list_item1));
        } else {
            convertView.setBackground(mContext.getResources().getDrawable(R.color.gray_list_item2));
        }

        if (item != null) {
            viewHolder.mName.setText(item.getString(ParseConstant.KEY_FULL_NAME));
            viewHolder.mEmail.setText(item.getEmail());
        }
        return convertView;
    }

    public Set<ParseUser> getSelectedUsers() {
        return mSelectedUsers;
    }

}
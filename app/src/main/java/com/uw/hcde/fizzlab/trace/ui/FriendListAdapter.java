package com.uw.hcde.fizzlab.trace.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.database.ParseConstant;
import com.uw.hcde.fizzlab.trace.database.ParseDataFactory;
import com.uw.hcde.fizzlab.trace.ui.profile.FriendsFragment;
import com.uw.hcde.fizzlab.trace.utility.TraceUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.app.PendingIntent.getActivity;

/**
 * @author tianchi
 */
public class FriendListAdapter extends ArrayAdapter<ParseUser>  {
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
        ImageView mDelete;
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
            viewHolder.mDelete = (ImageView) convertView.findViewById(R.id.friendList_delete);

            viewHolder.mDelete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //TraceUtil.showToast(mContext, "Delete?");
                    AlertDialog.Builder adb=new AlertDialog.Builder(getContext());
                    adb.setTitle("Delete?");
                    adb.setMessage("Are you sure you want to delete user: " +
                            items.get(position).getString(ParseConstant.KEY_FULL_NAME) +
                            " with email: " + items.get(position).getUsername() + " ?");
                    adb.setNegativeButton("Cancel", null);
                    adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ParseDataFactory.deleteFriend(ParseUser.getCurrentUser(), items.get(position));
                            items.remove(position);
                            notifyDataSetChanged();
                        }
                    });
                    adb.show();
                }
            });

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


        if (position % 2 == 0) {
            convertView.setBackground(mContext.getResources().getDrawable(R.color.gray_list_item1));
        } else {
            convertView.setBackground(mContext.getResources().getDrawable(R.color.gray_list_item2));
        }

        ParseUser item = getItem(position);
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
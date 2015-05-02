package com.uw.hcde.fizzlab.trace.ui.profile;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.database.ParseConstant;
import com.uw.hcde.fizzlab.trace.database.ParseDataFactory;
import com.uw.hcde.fizzlab.trace.database.ParseDrawing;
import com.uw.hcde.fizzlab.trace.database.callback.ParseRetrieveWalkedPathCallback;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

/**
 * Created by yellowleaf on 5/1/15.
 */
public class MyWalkingFragment extends Fragment implements ParseRetrieveWalkedPathCallback{

    private static final String TAG = "WalkedPathsFragment";
    private ListView mDrawnPathList;
    private List<ParseDrawing> mDrawings;
    private MyWalkedPathAdapter mAdapter;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drawn_paths, container, false);
        mDrawnPathList = (ListView) view.findViewById(R.id.list);
        mContext = getActivity();
        mAdapter = null;

        ParseDataFactory.retrieveMyWalkedPaths(ParseUser.getCurrentUser(), this);

        return view;
    }

    @Override
    public void retrieveWalkedPathCallBack(int returnCode, List<ParseDrawing> drawings) {
        if (returnCode == ParseConstant.SUCCESS && drawings.size() > 0) {
            mDrawings = drawings;
            Collections.sort(mDrawings);
            mAdapter = new MyWalkedPathAdapter(mContext, R.layout.list_item_my_walked_path, mDrawings);
            mDrawnPathList.setAdapter(mAdapter);
        }

    }

    private class MyWalkedPathAdapter extends ArrayAdapter<ParseDrawing> {
        private List<ParseDrawing> items;

        // Use view holder pattern to improve performance
        private class ViewHolder {
            TextView mTitle;
            TextView mDate;
            TextView mSender;
            ImageView mDelete;
        }

        public MyWalkedPathAdapter(Context context, int textViewResourceId, List<ParseDrawing> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.list_item_my_walked_path, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.mTitle = (TextView) convertView.findViewById(R.id.walkedpath_title);
                viewHolder.mDate = (TextView) convertView.findViewById(R.id.myDrawing_date);
                viewHolder.mSender = (TextView) convertView.findViewById(R.id.sender_name);
                viewHolder.mDelete = (ImageView) convertView.findViewById(R.id.walkedPath_delete);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            ParseDrawing item = items.get(position);
            if (item != null) {
                viewHolder.mTitle.setText(item.getDescription());
                viewHolder.mDate.setText(new SimpleDateFormat("MMM dd, yyyy").format(item.getCreatedAt()));
                viewHolder.mSender.setText(item.getCreatorRecord().getString(ParseConstant.KEY_FULL_NAME));
            }
            return convertView;
        }

    }
}

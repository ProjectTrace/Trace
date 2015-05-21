package com.uw.hcde.fizzlab.trace.ui.profile;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.dataContainer.TraceDataContainerReceiver;
import com.uw.hcde.fizzlab.trace.dataContainer.TracePoint;
import com.uw.hcde.fizzlab.trace.database.ParseConstant;
import com.uw.hcde.fizzlab.trace.database.ParseDataFactory;
import com.uw.hcde.fizzlab.trace.database.ParseDrawing;
import com.uw.hcde.fizzlab.trace.database.ParseWalkInfo;
import com.uw.hcde.fizzlab.trace.database.callback.ParseRetrieveWalkedPathCallback;
import com.uw.hcde.fizzlab.trace.navigation.MapActivity;
import com.uw.hcde.fizzlab.trace.navigation.ShowDrawingFragment;
import com.uw.hcde.fizzlab.trace.ui.drawing.DrawUtil;
import com.uw.hcde.fizzlab.trace.ui.walking.ParseDataContainer;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

/**
 * Created by yellowleaf on 5/1/15.
 */
public class MyWalkingFragment extends Fragment implements ParseRetrieveWalkedPathCallback{

    private static final String TAG = "WalkedPathsFragment";
    private ListView mDrawnPathList;
    private List<ParseWalkInfo> mWalkInfos;
    private MyWalkedPathAdapter mAdapter;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drawn_paths, container, false);
        mDrawnPathList = (ListView) view.findViewById(R.id.list);
        mContext = getActivity();
        mAdapter = null;

        ParseDataFactory.retrieveMyWalkedPaths(ParseUser.getCurrentUser(), this);
        mDrawnPathList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "User clicked walked path at position" + position);
                ParseDrawing drawing = mWalkInfos.get(position).getDrawing();

                List<TracePoint> points = ParseDataFactory.convertToTracePoints(drawing);
                TraceDataContainerReceiver.rawTracePoints = points;
                TraceDataContainerReceiver.trimmedTracePoints = DrawUtil.trimPoints(points);
                TraceDataContainerReceiver.description = drawing.getDescription();
                ParseDataContainer.currentUser = ParseUser.getCurrentUser();
                ParseDataContainer.drawing = drawing;
                TraceDataContainerReceiver.distance = mWalkInfos.get(position).getDis();

                Intent intent = new Intent(getActivity(), MapActivity.class);
                intent.putExtra("TAG", "alreadyWalked");
                startActivity(intent);

            }
        });
        return view;
    }

    @Override
    public void retrieveWalkedPathCallBack(int returnCode, List<ParseWalkInfo> walkInfoList) {
        if (returnCode == ParseConstant.SUCCESS && walkInfoList.size() > 0) {
            mWalkInfos = walkInfoList;
            //Collections.sort(mWalkInfos);
            mAdapter = new MyWalkedPathAdapter(mContext, R.layout.list_item_my_walked_path, mWalkInfos);
            mDrawnPathList.setAdapter(mAdapter);
        }

    }

    private class MyWalkedPathAdapter extends ArrayAdapter<ParseWalkInfo> {
        private List<ParseWalkInfo> items;

        // Use view holder pattern to improve performance
        private class ViewHolder {
            TextView mTitle;
            TextView mDate;
            TextView mSender;
            ImageView mDelete;
        }

        public MyWalkedPathAdapter(Context context, int textViewResourceId, List<ParseWalkInfo> items) {
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

            ParseDrawing item = items.get(position).getDrawing();

            if (item != null) {
                viewHolder.mTitle.setText(item.getDescription());
                viewHolder.mDate.setText(new SimpleDateFormat("MMM dd, yyyy").format(item.getCreatedAt()));
                viewHolder.mSender.setText(item.getCreatorRecord().getString(ParseConstant.KEY_FULL_NAME));
            }
            return convertView;
        }

    }
}

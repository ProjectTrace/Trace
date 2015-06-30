package com.uw.hcde.fizzlab.trace.ui.profile;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
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
import com.uw.hcde.fizzlab.trace.database.callback.ParseRetrieveDrawingCallback;
import com.uw.hcde.fizzlab.trace.database.callback.ParseRetrieveDrawnPathCallback;
import com.uw.hcde.fizzlab.trace.navigation.MapActivity;
import com.uw.hcde.fizzlab.trace.navigation.ShowDrawingFragment;
import com.uw.hcde.fizzlab.trace.ui.drawing.DrawUtil;
import com.uw.hcde.fizzlab.trace.ui.walking.ChooseDurationFragment;
import com.uw.hcde.fizzlab.trace.ui.walking.ParseDataContainer;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

/**
 * @author tianchi
 */
public class MyDrawingFragment extends Fragment implements ParseRetrieveDrawnPathCallback {

    private static final String TAG = "DrawnPathsFragment";
    private ListView mDrawnPathList;
    private List<ParseWalkInfo> mWalkInfos;
    private MyDrawingAdapter mAdapter;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drawn_paths, container, false);

        mDrawnPathList = (ListView) view.findViewById(R.id.list);
        mContext = getActivity();
        mAdapter = null;

        mDrawnPathList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "my drawn path on click");
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

        ParseDataFactory.retrieveMyDrawnPaths(ParseUser.getCurrentUser(), this);
        return view;
    }

    @Override
    public void retrieveDrawingsCallback(int returnCode, List<ParseWalkInfo> walkInfoList) {
        if (returnCode == ParseConstant.SUCCESS && walkInfoList.size() > 0) {
            mWalkInfos = walkInfoList;
            mAdapter = new MyDrawingAdapter(mContext, R.layout.list_item_my_drawn_path, mWalkInfos);
            mDrawnPathList.setAdapter(mAdapter);
        }
    }

    /**
     * @author tianchi
     */
    class MyDrawingAdapter extends ArrayAdapter<ParseWalkInfo> {

        private Context mContext;
        private List<ParseWalkInfo> items;

        // Use view holder pattern to improve performance
        private class ViewHolder {
            TextView mDescription;
            TextView mReceiverName;
            TextView mWalkedUser;
            TextView mDate;
            ImageView mDelete;
        }

        public MyDrawingAdapter(Context context, int textViewResourceId, List<ParseWalkInfo> items) {
            super(context, textViewResourceId, items);
            mContext = context;
            this.items = items;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.list_item_my_drawn_path, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.mDescription = (TextView) convertView.findViewById(R.id.drawing_title);
                viewHolder.mReceiverName = (TextView) convertView.findViewById(R.id.receiver_name);
                viewHolder.mDate = (TextView) convertView.findViewById(R.id.date);
                viewHolder.mDelete = (ImageView) convertView.findViewById(R.id.myDrawing_delete);
                viewHolder.mWalkedUser = (TextView) convertView.findViewById(R.id.walked_user);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final ParseDrawing drawing = getItem(position).getDrawing();

            viewHolder.mDelete.setOnClickListener(new View.OnClickListener() {

                /*
                    Didn't delete the drawing and annotation points in the cloud database.
                    Since receivers may need to use them.
                    We may have to deal with them backend deletion when there are lot of drawings.
                 */
                @Override
                public void onClick(View v) {
                    //TraceUtil.showToast(mContext, "Delete?");
                    AlertDialog.Builder adb=new AlertDialog.Builder(getContext());
                    adb.setTitle("Delete?");
                    adb.setMessage("Are you sure you want to delete drawing: " +
                            drawing.getDescription() +  " ?");
                    adb.setNegativeButton("Cancel", null);
                    adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ParseDataFactory.deleteMyWalkedPath(items.get(position));
                            items.remove(position);
                            notifyDataSetChanged();
                        }
                    });
                    adb.show();
                }
            });


            if (position % 2 == 0) {
                convertView.setBackground(mContext.getResources().getDrawable(R.color.gray_list_item1));
            } else {
                convertView.setBackground(mContext.getResources().getDrawable(R.color.gray_list_item2));
            }

            if (drawing != null) {
                viewHolder.mDescription.setText(drawing.getDescription());
                List<ParseUser> receivers = drawing.getReceiverRecord();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < receivers.size(); i++) {
                    sb.append(receivers.get(i).getString(ParseConstant.KEY_FULL_NAME));
                    if (i < receivers.size() - 1) {
                        sb.append(", ");
                    }
                }

                viewHolder.mDate.setText(new SimpleDateFormat("MMM dd, yyyy").format(drawing.getCreatedAt()));
                viewHolder.mReceiverName.setText(sb.toString());
                viewHolder.mWalkedUser.setText(items.get(position).getCreator().getString(ParseConstant.KEY_FULL_NAME));
            }
            return convertView;
        }
    }



}

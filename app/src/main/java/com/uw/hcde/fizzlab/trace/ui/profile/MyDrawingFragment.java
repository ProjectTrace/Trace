package com.uw.hcde.fizzlab.trace.ui.profile;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
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
import com.uw.hcde.fizzlab.trace.database.callback.ParseRetrieveDrawingCallback;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

/**
 * @author tianchi
 */
public class MyDrawingFragment extends Fragment implements ParseRetrieveDrawingCallback {

    private static final String TAG = "DrawnPathsFragment";
    private ListView mDrawnPathList;
    private MyDrawingAdapter mAdapter;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drawn_paths, container, false);

        mDrawnPathList = (ListView) view.findViewById(R.id.list);
        mContext = getActivity();
        mAdapter = null;

        ParseDataFactory.retrieveMyDrawings(ParseUser.getCurrentUser(), this);
        return view;
    }

    @Override
    public void retrieveDrawingsCallback(int returnCode, List<ParseDrawing> drawings) {
        if (returnCode == ParseConstant.SUCCESS && drawings.size() > 0) {
            Collections.sort(drawings);
            mAdapter = new MyDrawingAdapter(mContext, R.layout.list_item_my_drawing, drawings);
            mDrawnPathList.setAdapter(mAdapter);
        }
    }

    /**
     * @author tianchi
     */
    class MyDrawingAdapter extends ArrayAdapter<ParseDrawing> {

        private Context mContext;
        private List<ParseDrawing> items;

        // Use view holder pattern to improve performance
        private class ViewHolder {
            TextView mDescription;
            TextView mReceiverName;
            TextView mDate;
            ImageView mDelete;
        }

        public MyDrawingAdapter(Context context, int textViewResourceId, List<ParseDrawing> items) {
            super(context, textViewResourceId, items);
            mContext = context;
            this.items = items;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.list_item_my_drawing, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.mDescription = (TextView) convertView.findViewById(R.id.drawing_title);
                viewHolder.mReceiverName = (TextView) convertView.findViewById(R.id.receiver_name);
                viewHolder.mDate = (TextView) convertView.findViewById(R.id.date);
                viewHolder.mDelete = (ImageView) convertView.findViewById(R.id.myDrawing_delete);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final ParseDrawing item = getItem(position);

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
                            item.getDescription() +  " ?");
                    adb.setNegativeButton("Cancel", null);
                    adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
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

            if (item != null) {
                viewHolder.mDescription.setText(item.getDescription());
                List<ParseUser> receivers = item.getReceiverList();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < receivers.size(); i++) {
                    sb.append(receivers.get(i).getString(ParseConstant.KEY_FULL_NAME));
                    if (i < receivers.size() - 1) {
                        sb.append(", ");
                    }
                }

                viewHolder.mDate.setText(new SimpleDateFormat("MMM dd, yyyy").format(item.getCreatedAt()));
                viewHolder.mReceiverName.setText(sb.toString());
            }
            return convertView;
        }
    }
}

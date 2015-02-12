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
import com.uw.hcde.fizzlab.trace.database.ParseDataFactory;
import com.uw.hcde.fizzlab.trace.database.ParseDrawing;
import com.uw.hcde.fizzlab.trace.database.callback.ParseRetrieveDrawingCallback;

import java.text.SimpleDateFormat;
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
            mAdapter = new MyDrawingAdapter(mContext, R.layout.list_item_my_drawing, drawings);
            mDrawnPathList.setAdapter(mAdapter);
        }
    }

    /**
     * @author tianchi
     */
    class MyDrawingAdapter extends ArrayAdapter<ParseDrawing> {

        private Context mContext;

        // Use view holder pattern to improve performance
        private class ViewHolder {
            TextView mDescription;
            TextView mReceiverName;
            TextView mDate;
        }

        public MyDrawingAdapter(Context context, int textViewResourceId, List<ParseDrawing> items) {
            super(context, textViewResourceId, items);
            mContext = context;
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
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            ParseDrawing item = getItem(position);
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

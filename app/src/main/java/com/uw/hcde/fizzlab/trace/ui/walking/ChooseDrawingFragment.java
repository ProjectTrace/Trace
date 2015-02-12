package com.uw.hcde.fizzlab.trace.ui.walking;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.dataContainer.TraceDataContainerReceiver;
import com.uw.hcde.fizzlab.trace.dataContainer.TracePoint;
import com.uw.hcde.fizzlab.trace.database.ParseConstant;
import com.uw.hcde.fizzlab.trace.database.ParseDataFactory;
import com.uw.hcde.fizzlab.trace.database.ParseDrawing;
import com.uw.hcde.fizzlab.trace.database.callback.ParseRetrieveDrawingCallback;
import com.uw.hcde.fizzlab.trace.ui.BaseActivity;
import com.uw.hcde.fizzlab.trace.ui.drawing.DrawUtil;
import com.uw.hcde.fizzlab.trace.utility.TraceUtil;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Choose drawing fragments.
 *
 * @author tianchi
 */
public class ChooseDrawingFragment extends Fragment implements ParseRetrieveDrawingCallback {
    private static final String TAG = "ChooseDrawingFragment";

    private View mEmptyContentView;
    private ListView mDrawingListView;
    private ChooseDrawingAdapter mAdapter;
    private List<ParseDrawing> mDrawings;
    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_drawing, container, false);

        // Set navigation title
        ((BaseActivity) getActivity()).setNavigationTitle(R.string.choose_drawing);

        mEmptyContentView = view.findViewById(R.id.empty_message);
        mAdapter = null;
        mDrawingListView = (ListView) view.findViewById(R.id.choose_drawing_list);
        mDrawingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "drawing on click");
                List<TracePoint> points = ParseDataFactory.convertToTracePoints(mDrawings.get(position));
                TraceDataContainerReceiver.rawTracePoints = points;
                TraceDataContainerReceiver.trimmedTracePoints = DrawUtil.trimPoints(points);
                TraceDataContainerReceiver.description = mDrawings.get(position).getDescription();
                Log.d(TAG, "trimmed trace points: " + TraceDataContainerReceiver.trimmedTracePoints.size());

                // Fragment transaction
                Fragment fragment = new ChooseDurationFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.slide_in, R.anim.slide_out, R.anim.slide_in_backstack, R.anim.slide_out_backstack)
                        .add(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // Sets up progress dialog
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.progress_retrieving));
        mProgressDialog.show();

        mDrawings = null;
        ParseDataFactory.retrieveDrawings(ParseUser.getCurrentUser(), this);
        return view;
    }

    /**
     * Sets up empty content
     */
    private void setEmptyContent() {
        mEmptyContentView.setVisibility(View.VISIBLE);
        mDrawingListView.setVisibility(View.INVISIBLE);
    }

    // Retrieve drawings -> annotations -> creators
    @Override
    public void retrieveDrawingsCallback(int returnCode, List<ParseDrawing> drawings) {
        if (returnCode == ParseConstant.SUCCESS) {
            mDrawings = drawings;

            // Sorts the drawing in reverse created order
            Collections.sort(mDrawings, new Comparator<ParseDrawing>() {
                @Override
                public int compare(ParseDrawing lhs, ParseDrawing rhs) {
                    return -lhs.getCreatedAt().compareTo(rhs.getCreatedAt());
                }
            });

            Log.d(TAG, "drawing size: " + mDrawings.size());

            if (mDrawings.isEmpty()) {
                mProgressDialog.dismiss();
                setEmptyContent();

            } else {
                ParseDataFactory.retrieveAnnotations(mDrawings, this);
            }
        } else {
            showNetworkError();
            mProgressDialog.dismiss();
            setEmptyContent();
        }
    }


    @Override
    public void retrieveAnnotationsCallback(int returnCode) {
        if (returnCode == ParseConstant.SUCCESS) {
            ParseDataFactory.retrieveCreators(mDrawings, this);
        } else {
            showNetworkError();
            mProgressDialog.dismiss();
            setEmptyContent();
        }
    }

    @Override
    public void retrieveCreatorsCallback(int returnCode) {
        if (returnCode == ParseConstant.SUCCESS) {
            mProgressDialog.dismiss();

            // Finished retrieving
            mAdapter = new ChooseDrawingAdapter(getActivity(), R.layout.list_item_choose_drawing, mDrawings);
            mDrawingListView.setAdapter(mAdapter);

        } else {
            showNetworkError();
            mProgressDialog.dismiss();
            setEmptyContent();
        }
    }

    /**
     * Indicates network error while retrieving data
     */
    public void showNetworkError() {
        mProgressDialog.dismiss();
        TraceUtil.showToast(getActivity(), getString(R.string.toast_network_error));
        mEmptyContentView.setVisibility(View.VISIBLE);
    }

    private class ChooseDrawingAdapter extends ArrayAdapter<ParseDrawing> {

        // Use view holder pattern to improve performance
        private class ViewHolder {
            TextView mTitle;
            TextView mDate;
            TextView mSender;
        }

        public ChooseDrawingAdapter(Context context, int textViewResourceId, List<ParseDrawing> items) {
            super(context, textViewResourceId, items);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.list_item_choose_drawing, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.mTitle = (TextView) convertView.findViewById(R.id.item_title);
                viewHolder.mDate = (TextView) convertView.findViewById(R.id.item_date);
                viewHolder.mSender = (TextView) convertView.findViewById(R.id.item_sender);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            ParseDrawing item = getItem(position);
            if (position % 2 == 0) {
                convertView.setBackground(getResources().getDrawable(R.color.cyan_dark));
            } else {
                convertView.setBackground(getResources().getDrawable(R.color.cyan));
            }

            if (item != null) {
                viewHolder.mTitle.setText(item.getDescription());
                viewHolder.mDate.setText(new SimpleDateFormat("MMM dd, yyyy").format(item.getCreatedAt()));
                viewHolder.mSender.setText(item.getCreator().getUsername());
            }
            return convertView;
        }
    }
}

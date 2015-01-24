package com.uw.hcde.fizzlab.trace.userInterface.walking;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseUser;
import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.utility.TraceUtil;
import com.uw.hcde.fizzlab.trace.userInterface.drawing.DrawUtil;
import com.uw.hcde.fizzlab.trace.main.MainActivity;
import com.uw.hcde.fizzlab.trace.dataContainer.TraceDataContainer;
import com.uw.hcde.fizzlab.trace.dataContainer.TracePoint;
import com.uw.hcde.fizzlab.trace.database.ParseConstant;
import com.uw.hcde.fizzlab.trace.database.ParseDataFactory;
import com.uw.hcde.fizzlab.trace.database.ParseDrawing;
import com.uw.hcde.fizzlab.trace.database.callback.ParseRetrieveCallback;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Choose drawing fragments.
 *
 * @author tianchi
 */
public class ChooseDrawingFragment extends Fragment implements ParseRetrieveCallback {

    private static final String TAG = "ChooseDrawingFragment";

    private View mButtonHome;
    private View mButtonNext;

    // Drawing selector content
    private View mEmptyContentView;
    private View mContentView;

    // Drawing selector fields
    private View mButtonLeft;
    private View mButtonRight;
    private TextView mCreator;
    private TextView mDescription;
    private TextView mDate;

    private int mDrawingIndex;
    private List<ParseDrawing> mDrawings;
    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_drawing, container, false);

        // Set navigation title
        TextView title = (TextView) view.findViewById(R.id.navigation_title);
        title.setText(getString(R.string.choose_drawing));

        mEmptyContentView = view.findViewById(R.id.empty_message);
        mContentView = view.findViewById(R.id.content);
        mButtonLeft = view.findViewById(R.id.button_left);
        mButtonRight = view.findViewById(R.id.button_right);
        mCreator = (TextView) view.findViewById(R.id.creator);
        mDescription = (TextView) view.findViewById(R.id.description);
        mDate = (TextView) view.findViewById(R.id.date);
        mButtonNext = view.findViewById(R.id.button_next);
        mButtonHome = view.findViewById(R.id.navigation_button);
        setupButtons();

        // Sets up progress dialog
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.progress_retrieving));
        mProgressDialog.show();

        mDrawingIndex = 0;
        mDrawings = null;
        ParseDataFactory.retrieveDrawings(ParseUser.getCurrentUser(), this);

        return view;
    }

    /**
     * Sets up buttons
     */
    private void setupButtons() {
        mButtonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button next clicked");
                List<TracePoint> points = ParseDataFactory.convertToTracePoints(mDrawings.get(mDrawingIndex));
                TraceDataContainer.rawTracePoints = points;
                TraceDataContainer.trimmedTracePoints = DrawUtil.trimPoints(points);
                TraceDataContainer.description = mDrawings.get(mDrawingIndex).getDescription();
                Log.d(TAG, "trimmed trace points: " + TraceDataContainer.trimmedTracePoints.size());

                // Fragment transaction
                Fragment fragment = new ChooseDurationFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.slide_in, R.anim.slide_out, R.anim.slide_in_backstack, R.anim.slide_out_backstack)
                        .add(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });


        mButtonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawingIndex > 0) {
                    mDrawingIndex--;
                    updateDrawingDisplay();
                }
            }
        });

        mButtonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawingIndex < mDrawings.size() - 1) {
                    mDrawingIndex++;
                    updateDrawingDisplay();
                }
            }
        });
    }


    /**
     * Sets up drawing content
     */
    private void setDrawingContent() {
        mContentView.setVisibility(View.VISIBLE);
    }

    /**
     * Sets up empty content
     */
    private void setEmptyContent() {
        mEmptyContentView.setVisibility(View.VISIBLE);
        mButtonNext.setOnClickListener(null);
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
                mButtonNext.setVisibility(View.VISIBLE);
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
            updateDrawingDisplay();
            mProgressDialog.dismiss();
            setDrawingContent();
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

    /**
     * Sets drawing display
     */
    public void updateDrawingDisplay() {
        ParseDrawing drawing = mDrawings.get(mDrawingIndex);
        mCreator.setText(drawing.getCreator().getUsername());
        mDescription.setText(drawing.getDescription());

        // yyyy-MM-dd hh:mm:ss
        CharSequence s = DateFormat.format("yyyy-MM-dd", drawing.getCreatedAt());
        mDate.setText(s.toString());

        Log.d(TAG, "update drawing: " + mDrawingIndex);
    }
}

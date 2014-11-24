package com.uw.hcde.fizzlab.trace.controller.walk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.parse.ParseUser;
import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.controller.TraceUtil;
import com.uw.hcde.fizzlab.trace.model.object.TraceDataContainer;
import com.uw.hcde.fizzlab.trace.model.parse.ParseConstant;
import com.uw.hcde.fizzlab.trace.model.parse.ParseDataFactory;
import com.uw.hcde.fizzlab.trace.model.parse.ParseDrawing;
import com.uw.hcde.fizzlab.trace.model.parse.callback.ParseRetrieveCallback;

import java.util.Collections;
import java.util.List;

/**
 * Choose drawing
 *
 * @author tianchi
 */
public class ChooseDrawingActivity extends Activity implements ParseRetrieveCallback {

    private static final String TAG = "ChooseDrawingActivity";
    private static final String EXTRA_INT_DRAWING_IDENTIFIER = "drawing_identifier";

    private View mButtonBack;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_drawing);

        // Set navigation title
        TextView title = (TextView) findViewById(R.id.navigation_title);
        title.setText(getString(R.string.choose_drawing));

        mEmptyContentView = findViewById(R.id.empty_message);
        mContentView = findViewById(R.id.content);
        mButtonLeft = findViewById(R.id.button_left);
        mButtonRight = findViewById(R.id.button_right);
        mCreator = (TextView) findViewById(R.id.creator);
        mDescription = (TextView) findViewById(R.id.description);
        mDate = (TextView) findViewById(R.id.date);
        mButtonNext = findViewById(R.id.button_next);
        mButtonBack = findViewById(R.id.button_back);
        setupButtons();

        // Sets up progress dialog
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.progress_retrieving));
        mProgressDialog.show();

        mDrawingIndex = 0;
        mDrawings = null;
        ParseDataFactory.retrieveDrawings(ParseUser.getCurrentUser(), this);
    }

    /**
     * Sets up buttons
     */
    private void setupButtons() {
        mButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button next clicked");
                TraceDataContainer.sTracePoints = ParseDataFactory.convertToTracePoints(mDrawings.get(mDrawingIndex));
                Log.d(TAG, "trace points: " + TraceDataContainer.sTracePoints.size());
                Intent intent = new Intent(ChooseDrawingActivity.this, ChooseDurationActivity.class);
                startActivity(intent);
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
        mButtonNext.setVisibility(View.VISIBLE);
    }

    /**
     * Sets up empty content
     */
    private void setEmptyContent() {
        mEmptyContentView.setVisibility(View.VISIBLE);
        mButtonNext.setVisibility(View.INVISIBLE);
    }

    // Retrieve drawings -> annotations -> creators
    @Override
    public void retrieveDrawingsCallback(int returnCode, List<ParseDrawing> drawings) {
        if (returnCode == ParseConstant.SUCCESS) {
            mDrawings = drawings;
            Collections.reverse(mDrawings);
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
        TraceUtil.showToast(this, getString(R.string.toast_network_error));
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

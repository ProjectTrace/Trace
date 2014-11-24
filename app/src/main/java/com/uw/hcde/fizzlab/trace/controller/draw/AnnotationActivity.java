package com.uw.hcde.fizzlab.trace.controller.draw;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.ParseUser;
import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.controller.TraceUtil;
import com.uw.hcde.fizzlab.trace.controller.main.MainActivity;
import com.uw.hcde.fizzlab.trace.model.object.TracePoint;
import com.uw.hcde.fizzlab.trace.model.parse.ParseAnnotation;
import com.uw.hcde.fizzlab.trace.model.parse.ParseConstant;
import com.uw.hcde.fizzlab.trace.model.parse.ParseDataFactory;
import com.uw.hcde.fizzlab.trace.model.parse.callback.ParseSendCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity that handles annotation. Drawing activity
 * and annotation activity are separated in current design.
 *
 * @author tianchi
 */
public class AnnotationActivity extends Activity implements ParseSendCallback {

    private static final String TAG = "AnnotateActivity";
    private static final int TITLE_TEXT_SIZE_SP = 20;

    private View mButtonSend;
    private View mButtonBack;

    private List<Point> mRawPoints; // Used to display path
    private List<TracePoint> mTracePoints; // Trace points

    // Data to send on database
    private List<String> mReceiverNames;
    private List<ParseUser> mReceivers;
    private String mDescription;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annotate);

        mButtonBack = findViewById(R.id.button_back);
        mButtonSend = findViewById(R.id.button_send);

        Intent intent = getIntent();
        mRawPoints = intent.getParcelableArrayListExtra(DrawActivity.INTENT_EXTRA_RAW_POINTS);

        // More pipelines can be added here
        List<Point> normalizedPoints = DrawUtil.normalizePoints(mRawPoints);

        // Get tracePoints
        mTracePoints = DrawUtil.pointsToTracePoints(normalizedPoints);

        Log.d(TAG, "raw points size: " + mRawPoints.size());
        Log.d(TAG, "trace points size: " + mTracePoints.size());

        // Set navigation title
        TextView title = (TextView) findViewById(R.id.navigation_title);
        title.setText(getString(R.string.touch_to_annotate));
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, TITLE_TEXT_SIZE_SP);

        // Set up drawing view path
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.drawing_view_path);
        layout.addView(new DrawingViewPath(this));

        final AnnotationView annotationView = (AnnotationView) findViewById(R.id.drawing_view_annotation);
        annotationView.setTracePoints(mTracePoints);
        setupButtons();

        mReceivers = null;
        mReceiverNames = null;
        mDescription = null;
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.progress_sending));

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

        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button send clicked");

                // Send dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(AnnotationActivity.this);
                builder.setTitle(AnnotationActivity.this.getString(R.string.send));

                // Set up dialog view
                LayoutInflater inflater = (LayoutInflater) AnnotationActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.dialog_send, null);
                final EditText inputNames = (EditText) view.findViewById(R.id.input_username);
                final EditText inputDescription = (EditText) view.findViewById(R.id.input_description);

                // Build dialog
                builder.setView(view);
                builder.setPositiveButton(AnnotationActivity.this.getText(R.string.ok), null);
                builder.setNegativeButton(AnnotationActivity.this.getText(R.string.cancel), null);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

                // Sets positive button
                Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Get list of username
                        mReceiverNames = new ArrayList<String>();
                        for (String s : inputNames.getText().toString().split(",")) {
                            String name = s.trim();
                            if (name.length() > 0) {
                                mReceiverNames.add(name);
                            }
                        }

                        // Empty list
                        if (mReceiverNames.isEmpty()) {
                            TraceUtil.showToast(AnnotationActivity.this, getString(R.string.toast_enter_username));
                            return;
                        }

                        // Get description
                        mDescription = inputDescription.getText().toString().trim();
                        if (mDescription.length() == 0) {
                            TraceUtil.showToast(AnnotationActivity.this, getString(R.string.toast_enter_description));
                            return;
                        }

                        // TODO: more pipelines on tracePoints goes here

                        // Send data
                        sendData();
                        alertDialog.dismiss();
                    }
                });
            }
        });
    }


    /**
     * Sends all data to parse database and sets up progress dialog
     * Get name -> send annotation -> send drawing
     */
    private void sendData() {
        mProgressDialog.show();
        ParseDataFactory.convertNameToParseUser(mReceiverNames, this);
    }

    @Override
    public void convertNameToUserCallback(int returnCode, List<ParseUser> users) {
        if (returnCode == ParseConstant.SUCCESS) {
            mReceivers = users;

            // Contains invalid names
            if (mReceivers.size() != mReceiverNames.size()) {

                // Gets failed names
                List<String> failedNames = new ArrayList<String>(mReceiverNames);
                for (ParseUser user : mReceivers) {
                    if (mReceiverNames.contains(user.getUsername())) {
                        failedNames.remove(user.getUsername());
                    }
                }

                // Finds out invalid usernames
                String msg = getString(R.string.toast_invalid_username);
                for (int i = 0; i < failedNames.size(); i++) {
                    msg += failedNames.get(i);
                    if (i < failedNames.size() - 1) {
                        msg += ", ";
                    }
                }

                mProgressDialog.dismiss();
                TraceUtil.showToast(this, msg);
            } else {
                ParseDataFactory.sendAnnotation(mTracePoints, this);
            }

        } else {
            mProgressDialog.dismiss();
            TraceUtil.showToast(this, getString(R.string.toast_network_error));
        }
    }

    @Override
    public void sendAnnotationCallback(int returnCode, List<ParseAnnotation> annotations) {
        if (returnCode == ParseConstant.SUCCESS) {
            ParseDataFactory.sendDrawing(mDescription, mReceivers, mTracePoints, annotations, this);
        } else {
            mProgressDialog.dismiss();
            TraceUtil.showToast(this, getString(R.string.toast_network_error));
        }
    }

    @Override
    public void sendDrawingCallback(int returnCode) {
        mProgressDialog.dismiss();
        if (returnCode == ParseConstant.SUCCESS) {
            TraceUtil.showToast(AnnotationActivity.this, getString(R.string.toast_success));

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(AnnotationActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }, TraceUtil.TOAST_MESSAGE_TIME);

        } else {
            TraceUtil.showToast(this, getString(R.string.toast_network_error));
        }
    }

    /**
     * Inner class to draw the path obtained from DrawingView activity, using rawPoints.
     */
    private class DrawingViewPath extends View {
        private Paint mPaint;
        private Path mPath;

        public DrawingViewPath(Context context) {
            super(context);
            mPaint = new Paint();

            // Set up paint style
            mPaint.setAntiAlias(true);
            mPaint.setStrokeWidth(6f);
            mPaint.setColor(getResources().getColor(R.color.transparent_white1));
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeCap(Paint.Cap.ROUND);

            // Sets up path given points, using Quadratic Bézier curves
            mPath = DrawUtil.getBezierPath(mRawPoints);
            Point firstPoint = mRawPoints.get(0);
            mPath.lineTo(firstPoint.x, firstPoint.y);
        }

        @Override
        public void onDraw(Canvas canvas) {
            canvas.drawPath(mPath, mPaint);
        }
    }
}

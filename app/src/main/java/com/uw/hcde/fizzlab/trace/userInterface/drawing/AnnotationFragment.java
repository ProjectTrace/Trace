package com.uw.hcde.fizzlab.trace.userInterface.drawing;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.dataContainer.TracePoint;
import com.uw.hcde.fizzlab.trace.database.ParseAnnotation;
import com.uw.hcde.fizzlab.trace.database.ParseConstant;
import com.uw.hcde.fizzlab.trace.database.ParseDataFactory;
import com.uw.hcde.fizzlab.trace.database.callback.ParseSendCallback;
import com.uw.hcde.fizzlab.trace.userInterface.BaseActivity;
import com.uw.hcde.fizzlab.trace.utility.TraceUtil;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Fragment that handles annotation.
 *
 * @author tianchi
 */
public class AnnotationFragment extends Fragment implements ParseSendCallback {
    public static final String TAG = "AnnotationFragment";

    private View mButtonSend;
    private List<Point> mRawPoints; // Used to display path
    private List<TracePoint> mTracePoints; // Trace points

    // Data to send on database
    private List<String> mReceiverNames;
    private List<ParseUser> mReceivers;
    private String mDescription;
    private ProgressDialog mProgressDialog;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_annotation, container, false);
        mButtonSend = view.findViewById(R.id.button_send);

        Bundle args = getArguments();
        mRawPoints = args.getParcelableArrayList(DrawFragment.KEY_RAW_POINTS);

        // More pipelines can be added here
        List<Point> normalizedPoints = DrawUtil.normalizePoints(mRawPoints);

        // Get trimmedTracePoints
        mTracePoints = DrawUtil.pointsToTracePoints(normalizedPoints);

        Log.d(TAG, "raw points size: " + mRawPoints.size());
        Log.d(TAG, "trace points size: " + mTracePoints.size());

        // Set navigation title
        ((BaseActivity) getActivity()).setNavigationTitle(R.string.touch_to_annotate,
                BaseActivity.NAVIGATION_BAR_TITLE_SIZE_SMALL_SP);

        // Set up drawing view path
        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.drawing_view_path);
        layout.addView(new DrawingViewPath(getActivity()));

        final AnnotationView annotationView = (AnnotationView) view.findViewById(R.id.drawing_view_annotation);
        annotationView.setTracePoints(mTracePoints);
        setupButtons();

        mReceivers = null;
        mReceiverNames = null;
        mDescription = null;
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.progress_sending));
        mProgressDialog.setCanceledOnTouchOutside(false);

        return view;

    }

    /**
     * Sets up buttons
     */
    private void setupButtons() {

        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button send clicked");

                final MaterialDialog dialog = new MaterialDialog(getActivity());
                dialog.setTitle(R.string.send);

                // Set up dialog view
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.dialog_send, null);
                final EditText inputNames = (EditText) view.findViewById(R.id.input_username);
                final EditText inputDescription = (EditText) view.findViewById(R.id.input_description);

                // Build dialog
                dialog.setContentView(view);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setPositiveButton(R.string.ok, new View.OnClickListener() {
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
                            TraceUtil.showToast(getActivity(), getString(R.string.toast_enter_username));
                            return;
                        }

                        // Get description
                        mDescription = inputDescription.getText().toString().trim();
                        if (mDescription.length() == 0) {
                            TraceUtil.showToast(getActivity(), getString(R.string.toast_enter_description));
                            return;
                        }

                        // Send data
                        sendData();
                        dialog.dismiss();
                    }
                });

                dialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
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
                StringBuilder sb = new StringBuilder();
                sb.append(getString(R.string.toast_invalid_username));
                sb.append(" ");
                for (int i = 0; i < failedNames.size(); i++) {
                    sb.append(failedNames.get(i));
                    if (i < failedNames.size() - 1) {
                        sb.append(", ");
                    }
                }

                mProgressDialog.dismiss();
                TraceUtil.showToast(getActivity(), sb.toString());
            } else {
                ParseDataFactory.sendAnnotation(mTracePoints, this);
            }

        } else {
            mProgressDialog.dismiss();
            TraceUtil.showToast(getActivity(), getString(R.string.toast_network_error));
        }
    }

    @Override
    public void sendAnnotationCallback(int returnCode, List<ParseAnnotation> annotations) {
        if (returnCode == ParseConstant.SUCCESS) {
            ParseDataFactory.sendDrawing(mDescription, mReceivers, mTracePoints, annotations, this);
        } else {
            mProgressDialog.dismiss();
            TraceUtil.showToast(getActivity(), getString(R.string.toast_network_error));
        }
    }

    @Override
    public void sendDrawingCallback(int returnCode) {
        if (returnCode == ParseConstant.SUCCESS) {
            mProgressDialog.dismiss();
            TraceUtil.showToast(getActivity(), getString(R.string.toast_success));

            // send notification to users
            ParsePush parsePush = new ParsePush();
            ParseQuery pQuery = ParseInstallation.getQuery();
            pQuery.whereContainedIn("username", mReceiverNames);
            parsePush.sendMessageInBackground("Hello!", pQuery);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getActivity().finish();
                }
            }, TraceUtil.TOAST_MESSAGE_TIME);

        } else {
            mProgressDialog.dismiss();
            TraceUtil.showToast(getActivity(), getString(R.string.toast_network_error));
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

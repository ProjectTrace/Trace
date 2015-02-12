package com.uw.hcde.fizzlab.trace.ui.drawing;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.dataContainer.TraceDataContainerSender;
import com.uw.hcde.fizzlab.trace.dataContainer.TracePoint;
import com.uw.hcde.fizzlab.trace.ui.BaseActivity;

import java.util.List;

/**
 * Fragment that handles annotation.
 *
 * @author tianchi
 */
public class AnnotationFragment extends Fragment {
    public static final String TAG = "AnnotationFragment";

    private View mButtonNext;
    private List<Point> mRawPoints; // Used to display path
    private List<TracePoint> mTracePoints; // Trace points


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_annotation, container, false);
        mButtonNext = view.findViewById(R.id.button_next);


        mRawPoints = TraceDataContainerSender.rawPoints;
        mTracePoints = DrawUtil.pointsToTracePoints(DrawUtil.normalizePoints(mRawPoints));
        TraceDataContainerSender.tracePoints = mTracePoints;

        Log.d(TAG, "raw points size: " + mRawPoints.size());
        Log.d(TAG, "trace points size: " + mTracePoints.size());

        // Set navigation title
        ((BaseActivity) getActivity()).setNavigationTitle(R.string.draw_step_2);

        // Set up drawing view path
        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.drawing_view_path);
        layout.addView(new DrawingViewPath(getActivity()));

        final AnnotationView annotationView = (AnnotationView) view.findViewById(R.id.drawing_view_annotation);
        annotationView.setTracePoints(mTracePoints);
        setupButtons();

        return view;

    }

    /**
     * Sets up buttons
     */
    private void setupButtons() {
        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new SelectFriendFragment();

                // Fragment transaction
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.slide_in, R.anim.slide_out, R.anim.slide_in_backstack, R.anim.slide_out_backstack)
                        .add(R.id.fragment_container, fragment, DrawActivity.SELECT_FRIEND_FRAGMENT_TAG)
                        .addToBackStack(null)
                        .commit();
            }
        });

//        mButtonNext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "Button send clicked");
//
//                final MaterialDialog dialog = new MaterialDialog(getActivity());
//                dialog.setTitle(R.string.send);
//
//                // Set up dialog view
//                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                View view = inflater.inflate(R.layout.dialog_send, null);
//                final EditText inputNames = (EditText) view.findViewById(R.id.input_username);
//                final EditText inputDescription = (EditText) view.findViewById(R.id.input_description);
//
//                // Build dialog
//                dialog.setContentView(view);
//                dialog.setCanceledOnTouchOutside(true);
//                dialog.setPositiveButton(R.string.ok, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        // Get list of username
//                        mReceiverNames = new ArrayList<String>();
//                        for (String s : inputNames.getText().toString().split(",")) {
//                            String name = s.trim();
//                            if (name.length() > 0) {
//                                mReceiverNames.add(name);
//                            }
//                        }
//
//                        // Empty list
//                        if (mReceiverNames.isEmpty()) {
//                            TraceUtil.showToast(getActivity(), getString(R.string.toast_enter_username));
//                            return;
//                        }
//
//                        // Get description
//                        mDescription = inputDescription.getText().toString().trim();
//                        if (mDescription.length() == 0) {
//                            TraceUtil.showToast(getActivity(), getString(R.string.toast_enter_description));
//                            return;
//                        }
//
//                        // Send data
//                        sendData();
//                        dialog.dismiss();
//                    }
//                });
//
//                dialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
//                dialog.show();
//            }
//        });
    }

    /**
     * Sends all data to parse database and sets up progress dialog
     * Get name -> send annotation -> send drawing
     //     */
//    private void sendData() {
//        mProgressDialog.show();
//        ParseDataFactory.convertNameToParseUser(mReceiverNames, this);
//    }

//    @Override
//    public void convertNameToUserCallback(int returnCode, List<ParseUser> users) {
//        if (returnCode == ParseConstant.SUCCESS) {
//            mReceivers = users;
//
//            // Contains invalid names
//            if (mReceivers.size() != mReceiverNames.size()) {
//
//                // Gets failed names
//                List<String> failedNames = new ArrayList<String>(mReceiverNames);
//                for (ParseUser user : mReceivers) {
//                    if (mReceiverNames.contains(user.getUsername())) {
//                        failedNames.remove(user.getUsername());
//                    }
//                }
//
//                // Finds out invalid usernames
//                StringBuilder sb = new StringBuilder();
//                sb.append(getString(R.string.toast_invalid_username));
//                sb.append(" ");
//                for (int i = 0; i < failedNames.size(); i++) {
//                    sb.append(failedNames.get(i));
//                    if (i < failedNames.size() - 1) {
//                        sb.append(", ");
//                    }
//                }
//
//                mProgressDialog.dismiss();
//                TraceUtil.showToast(getActivity(), sb.toString());
//            } else {
//                ParseDataFactory.sendAnnotation(mTracePoints, this);
//            }
//
//        } else {
//            mProgressDialog.dismiss();
//            TraceUtil.showToast(getActivity(), getString(R.string.toast_network_error));
//        }
//    }

//    @Override
//    public void sendAnnotationCallback(int returnCode, List<ParseAnnotation> annotations) {
//        if (returnCode == ParseConstant.SUCCESS) {
//            ParseDataFactory.sendDrawing(mDescription, mReceivers, mTracePoints, annotations, this);
//        } else {
//            mProgressDialog.dismiss();
//            TraceUtil.showToast(getActivity(), getString(R.string.toast_network_error));
//        }
//    }

//    @Override
//    public void sendDrawingCallback(int returnCode) {
//        if (returnCode == ParseConstant.SUCCESS) {
//            mProgressDialog.dismiss();
//            TraceUtil.showToast(getActivity(), getString(R.string.toast_success));
//
//            // send notification to users
//            ParseQuery pushQuery = ParseInstallation.getQuery();
//            pushQuery.whereContainedIn("username", mReceiverNames); // Set the target email
//
//            // Send push notification to query by email
//            ParsePush push = new ParsePush();
//            push.setQuery(pushQuery);
//            push.setMessage(ParseUser.getCurrentUser().getUsername() + " send you a new drawing: " + mDescription);
//            push.sendInBackground(new SendCallback() {
//                @Override
//                public void done(ParseException e) {
//                    if (e == null) {
//                        Log.d(TAG, "Success send welcome notification");
//                    } else {
//                        Log.e(TAG, "Failed send welcome notification");
//                    }
//                }
//            });
//
//            //
//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    getActivity().finish();
//                }
//            }, TraceUtil.TOAST_MESSAGE_TIME);
//
//        } else {
//            mProgressDialog.dismiss();
//            TraceUtil.showToast(getActivity(), getString(R.string.toast_network_error));
//        }
//    }

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

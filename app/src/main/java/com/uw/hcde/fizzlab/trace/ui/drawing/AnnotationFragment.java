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

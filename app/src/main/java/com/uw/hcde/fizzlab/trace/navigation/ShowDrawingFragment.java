package com.uw.hcde.fizzlab.trace.navigation;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.userInterface.drawing.DrawUtil;
import com.uw.hcde.fizzlab.trace.dataContainer.TraceDataContainer;

import java.util.List;


/**
 * Shows drawing at receiver side.
 *
 * @author tianchi
 */
public class ShowDrawingFragment extends Fragment {

    private static final String TAG = "ShowDrawingFragment";

    // Main buttons
    private TextView mButtonBack;
    private List<Point> mPoints;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_drawing, container, false);

        // Set navigation title and back button
        TextView title = (TextView) view.findViewById(R.id.navigation_title);
        title.setText(getString(R.string.show_drawing));
        mButtonBack = (TextView) view.findViewById(R.id.navigation_button);
        mButtonBack.setText(getString(R.string.back));
        mButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                fm.popBackStack();
                getFragmentManager().findFragmentById(R.id.map).getView().setVisibility(View.VISIBLE);
            }
        });
        mPoints = DrawUtil.tracePointsToPoints(TraceDataContainer.rawTracePoints);

        // Set up drawing view path
        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.drawing_view_path);
        layout.addView(new DrawingViewPath(getActivity()));

        return view;
    }

    /**
     * Inner class to draw the path obtained from DrawingView activity, using rawPoints.
     * TODO: possible code reuse from drawing module.
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
            mPath = DrawUtil.getBezierPath(mPoints);
            Point firstPoint = mPoints.get(0);
            mPath.lineTo(firstPoint.x, firstPoint.y);
        }

        @Override
        public void onDraw(Canvas canvas) {
            canvas.drawPath(mPath, mPaint);
        }
    }
}
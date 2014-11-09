package com.uw.hcde.fizzlab.trace.controller.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.util.DrawUtil;
import com.uw.hcde.fizzlab.trace.util.TraceUtil;

import java.util.ArrayList;

/**
 * A custom view to for drawing patterns.
 *
 * @author tianchi
 */
public class DrawingView extends View {
    private static final String TAG = "DrawingView";

    private static final int MIN_PIXEL_THRESHOLD = 500;

    private Paint mPaint;
    private Boolean mIsFinish;
    private Context mAppContext;

    // To connect to initial point
    private ArrayList<Point> points;
    private PathMeasure mPathMeasure;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mAppContext = getContext();

        mPaint = new Paint();
        mIsFinish = false;
        mPathMeasure = new PathMeasure();
        points = new ArrayList<Point>();

        // Set up paint style
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(6f);
        mPaint.setColor(getResources().getColor(R.color.transparent_white1));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // Use Quadratic Bézier curves to draw path
        Path path = DrawUtil.getBezierPath(points);
        if (mIsFinish) {
            // Connect last point to fist point
            Point firstPoint = points.get(0);
            path.lineTo(firstPoint.x, firstPoint.y);

            // Measure path length
            mPathMeasure.setPath(path, false);
            float len = mPathMeasure.getLength();
            Log.d(TAG, "Measured length: " + len);
            if (len < MIN_PIXEL_THRESHOLD) {
                TraceUtil.showToast(mAppContext, mAppContext.getString(R.string.toast_draw_larger));
                clear();
                return;
            }
        }
        canvas.drawPath(path, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // No more drawing if finished
        if (mIsFinish || TraceUtil.isShowingToast()) {
            return true;
        }

        int eventX = (int) event.getX();
        int eventY = (int) event.getY();
        Point currentPoint = new Point(eventX, eventY);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;

            case MotionEvent.ACTION_MOVE:
                if (points.isEmpty()) {
                    return false;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (points.isEmpty()) {
                    return false;
                }
                mIsFinish = true;
                break;

            default:
                return false;
        }
        points.add(currentPoint);

        // Schedules a repaint.
        invalidate();
        return true;
    }

    /**
     * Get raw drawing points.
     */
    public ArrayList<Point> getPoints() {
        return points;
    }

    /**
     * Checks if drawing is valid, more condition can be added here.
     *
     * @return true or false
     */
    public boolean isValid() {
        if (points.size() == 0) {
            TraceUtil.showToast(mAppContext, mAppContext.getString(R.string.toast_draw_something));
            return false;
        }
        return true;
    }

    /**
     * Clear content
     */
    public void clear() {
        mIsFinish = false;
        points.clear();
        invalidate();
    }
}

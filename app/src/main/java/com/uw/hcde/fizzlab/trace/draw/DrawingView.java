package com.uw.hcde.fizzlab.trace.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.uw.hcde.fizzlab.trace.R;

import java.util.ArrayList;

/**
 * A custom view to for drawing patterns
 *
 * @author tianchi
 */
public class DrawingView extends View {
    private static final String TAG = "DrawingView";

    private static final int MIN_PIXEL_THRESHOLD = 500;

    private Paint mPaint;
    private Path mPath;
    private Boolean mIsFinish;
    private Context mAppContext;

    // To connect to initial SimplePoint
    private ArrayList<Point> points;
    private int mPixelLength;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mAppContext = getContext();

        mPaint = new Paint();
        mPath = new Path();
        mIsFinish = false;
        mPixelLength = 0;
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
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // No more drawing if finished
        if (mIsFinish) {
            return true;
        }

        int eventX = (int) event.getX();
        int eventY = (int) event.getY();
        Point currentPoint = new Point(eventX, eventY);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPath.moveTo(eventX, eventY);
                break;

            case MotionEvent.ACTION_MOVE:
                mPath.lineTo(eventX, eventY);
                Point prevPoint = points.get(points.size() - 1);
                mPixelLength += (int) getEuclideanDistance(prevPoint, currentPoint);
                break;

            case MotionEvent.ACTION_UP:
                Point firstPoint = points.get(0);
                int length = (int) getEuclideanDistance(currentPoint, firstPoint);
                if (length + mPixelLength > MIN_PIXEL_THRESHOLD) {
                    mIsFinish = true;
                    mPath.lineTo(firstPoint.x, firstPoint.y);
                    mPixelLength += length;
                } else {

                    // Drawing is too small. This check needed to be done here
                    // in case the user could not see what he/she draw while
                    // system prevents user form drawing again.
                    Toast toast = Toast.makeText(mAppContext,
                            mAppContext.getString(R.string.toast_draw_larger), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    clear();
                    return true;
                }
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
     * Returns Euclidean distance between two SimplePoints.
     *
     * @param p1
     * @param p2
     */
    private double getEuclideanDistance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    /**
     * Completes drawing
     */
    public void complete() {
        DrawActivity.sDrawingData.points = points;
        DrawActivity.sDrawingData.length = mPixelLength;
    }

    /**
     * Checks if drawing is valid, more condition can be added here
     *
     * @return true or false
     */
    public boolean isValid() {
        String message = null;
        if (mPixelLength == 0) {
            message = mAppContext.getString(R.string.toast_draw_something);
        }

        if (message != null) {
            Toast toast = Toast.makeText(mAppContext, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return false;
        } else {
            return true;
        }
    }

    /**
     * Clear content
     */
    public void clear() {
        mPath.reset();
        mIsFinish = false;
        mPixelLength = 0;
        points.clear();
        invalidate();
    }
}

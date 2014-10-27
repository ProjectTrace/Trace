package com.uw.hcde.fizzlab.trace.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
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

    private static final int MIN_PIXEL_THRESHOLD = 100;

    private Paint mPaint;
    private Path mPath;
    private Boolean mIsFinish;
    private Context mAppContext;

    // To connect to initial SimplePoint
    private ArrayList<Point> points;
    private float mPixelLength;

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
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setAlpha(200);
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
                Point prevSimplePoint = points.get(points.size() - 1);
                mPixelLength += (int) getEuclideanDistance(prevSimplePoint, currentPoint);
                break;

            case MotionEvent.ACTION_UP:
                if (mPixelLength > MIN_PIXEL_THRESHOLD) {
                    mIsFinish = true;
                    Point firstSimplePoint = points.get(0);
                    mPath.lineTo(firstSimplePoint.x, firstSimplePoint.y);
                } else {
                    // Drawing is too small
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

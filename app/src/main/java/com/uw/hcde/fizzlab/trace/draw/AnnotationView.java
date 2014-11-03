package com.uw.hcde.fizzlab.trace.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.uw.hcde.fizzlab.trace.R;

import java.util.ArrayList;

/**
 * Adds annotation to the existing path
 *
 * @author tianchi
 */
public class AnnotationView extends View {

    private static final int CIRCLE_RADIUS_BG_DP = 8;
    private static final int CIRCLE_RADIUS_SMALL_DP = 6;

    private ArrayList<Point> mPathPoints;
    private ArrayList<Point> mAnnotationPoints;
    private Paint mPaint;

    public AnnotationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mAnnotationPoints = new ArrayList<Point>();

        // Set up paint
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(getResources().getColor(R.color.main_cyan2));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStyle(Paint.Style.FILL);
    }

    public void setTransformedPoints(ArrayList<Point> points) {
        mPathPoints = points;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (Point p : mAnnotationPoints) {
            drawCircle(canvas, p);
        }
    }

    /**
     * Draws a circle indicated an annotation
     *
     * @param canvas
     * @param p
     */
    private void drawCircle(Canvas canvas, Point p) {
        float bg = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                CIRCLE_RADIUS_BG_DP, getResources().getDisplayMetrics());
        float small = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                CIRCLE_RADIUS_SMALL_DP, getResources().getDisplayMetrics());

        mPaint.setColor(getResources().getColor(R.color.main_cyan2));
        canvas.drawCircle(p.x, p.y, bg, mPaint);
        mPaint.setColor(getResources().getColor(R.color.main_cyan1));
        canvas.drawCircle(p.x, p.y, small, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventX = (int) event.getX();
        int eventY = (int) event.getY();
        Point currentPoint = new Point(eventX, eventY);

        if (event.getAction() == MotionEvent.ACTION_DOWN && isOnPath(currentPoint)) {
            mAnnotationPoints.add(currentPoint);
            invalidate();
            return true;
        }
        return false;
    }

    //TODO: this part needs to be revised

    /**
     * Checks if a touch point is on path.
     * @param p touch point
     * @return
     */
    private boolean isOnPath(Point p) {

        return true;
    }
}

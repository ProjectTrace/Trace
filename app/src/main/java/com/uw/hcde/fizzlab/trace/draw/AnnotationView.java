package com.uw.hcde.fizzlab.trace.draw;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.util.TraceUtil;

import java.util.ArrayList;

/**
 * Adds annotation to the existing path
 *
 * @author tianchi
 */
public class AnnotationView extends View {

    private static final String TAG = "AnnotationView";

    private static final int CIRCLE_RADIUS_BG_DP = 8;
    private static final int CIRCLE_RADIUS_SMALL_DP = 6;
    private static final int ANNOTATION_DETECTION_RADIUS_DP = 12;

    private static int ANNOTATION_TYPE_NEW = 0;
    private static int ANNOTATION_TYPE_UPDATE = 1;

    private ArrayList<Point> mPathPoints;
    private ArrayList<AnnotationPoint> mAnnotationPoints;
    private Paint mPaint;
    private Context mAppContext;

    private float mCircle_radius_bg;
    private float mCircle_radius_small;
    private float mDetection_radius;

    public AnnotationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mAppContext = context;
        mAnnotationPoints = new ArrayList<AnnotationPoint>();

        mCircle_radius_bg = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                CIRCLE_RADIUS_BG_DP, getResources().getDisplayMetrics());
        mCircle_radius_small = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                CIRCLE_RADIUS_SMALL_DP, getResources().getDisplayMetrics());
        mDetection_radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                ANNOTATION_DETECTION_RADIUS_DP, getResources().getDisplayMetrics());

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
        for (AnnotationPoint p : mAnnotationPoints) {
            drawCircle(canvas, p.getPoint());
        }
    }

    /**
     * Draws a circle indicated an annotation
     *
     * @param canvas
     * @param p
     */
    private void drawCircle(Canvas canvas, Point p) {
        mPaint.setColor(getResources().getColor(R.color.main_cyan2));
        canvas.drawCircle(p.x, p.y, mCircle_radius_bg, mPaint);
        mPaint.setColor(getResources().getColor(R.color.main_cyan1));
        canvas.drawCircle(p.x, p.y, mCircle_radius_small, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "on Touch");
        int eventX = (int) event.getX();
        int eventY = (int) event.getY();
        Point p1 = new Point(eventX, eventY);

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            boolean isOnPath = false;
            for (int i = 0; i < mPathPoints.size(); i++) {
                Point p2 = mPathPoints.get(i);
                if (p1.x >= p2.x - mDetection_radius && p1.x <= p2.x + mDetection_radius
                        && p1.y >= p2.y - mDetection_radius && p1.y <= p2.y + mDetection_radius) {

                    int annotationIndex = findAnnotationIndex(p2);
                    int type;
                    if (annotationIndex == -1) {
                        type = ANNOTATION_TYPE_NEW;
                        mAnnotationPoints.add(new AnnotationPoint(p2, ""));
                        annotationIndex = mAnnotationPoints.size() - 1;
                        invalidate();
                    } else {
                        type = ANNOTATION_TYPE_UPDATE;
                    }

                    promptInput(annotationIndex, type);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns annotation points
     *
     * @return
     */
    public ArrayList<AnnotationPoint> getAnnotationPoints() {
        return mAnnotationPoints;
    }

    /**
     * Finds annotation index, -1 if not found
     *
     * @param p
     */
    private int findAnnotationIndex(Point p) {
        for (int j = 0; j < mAnnotationPoints.size(); j++) {
            Point p2 = mAnnotationPoints.get(j).getPoint();
            if (p.x == p2.x && p.y == p2.y) {
                return j;
            }
        }
        return -1;
    }

    /**
     * Prompts annotation input.
     */
    private void promptInput(final int annotationIndex, final int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mAppContext);
        builder.setTitle(mAppContext.getString(R.string.annotate));

        // Set up edit text
        final EditText input = new EditText(mAppContext);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setLines(3);
        input.setSingleLine(false);
        input.setGravity(Gravity.LEFT | Gravity.TOP);

        String msg = mAnnotationPoints.get(annotationIndex).getMsg();
        input.setText(msg);
        input.setSelection(msg.length());
        builder.setView(input);

        // Dialog buttons
        builder.setPositiveButton(mAppContext.getText(R.string.set), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();
                if (text.length() != 0) {
                    AnnotationPoint annotationPoint = mAnnotationPoints.get(annotationIndex);
                    annotationPoint.setMsg(text);

                } else {
                    if (type == ANNOTATION_TYPE_NEW) {
                        mAnnotationPoints.remove(annotationIndex);
                    }
                    TraceUtil.showToast(mAppContext, mAppContext.getString(R.string.toast_enter_annotation));
                }
                invalidate();
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface arg0) {
                if (type == ANNOTATION_TYPE_NEW) {
                    mAnnotationPoints.remove(annotationIndex);
                    invalidate();
                }
            }
        });

        builder.setNegativeButton(mAppContext.getText(R.string.delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAnnotationPoints.remove(annotationIndex);
                invalidate();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}

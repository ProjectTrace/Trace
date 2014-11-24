package com.uw.hcde.fizzlab.trace.controller.draw;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.controller.TraceUtil;
import com.uw.hcde.fizzlab.trace.model.object.TraceAnnotation;
import com.uw.hcde.fizzlab.trace.model.object.TracePoint;

import java.util.List;

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

    private List<TracePoint> mTracePoints;

    private Paint mPaint;
    private Context mAppContext;

    private float mCircle_radius_bg;
    private float mCircle_radius_small;
    private float mDetection_radius;

    public AnnotationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mAppContext = context;

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

    public void setTracePoints(List<TracePoint> points) {
        mTracePoints = points;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (TracePoint p : mTracePoints) {
            if (p.annotation != null) {
                drawCircle(canvas, p.point);
            }
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
        int eventX = (int) event.getX();
        int eventY = (int) event.getY();
        Point currPoint = new Point(eventX, eventY);

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            for (int i = 0; i < mTracePoints.size(); i++) {
                TracePoint tracePoint = mTracePoints.get(i);
                Point p = tracePoint.point;

                // If on path
                if (currPoint.x >= p.x - mDetection_radius && currPoint.x <= p.x + mDetection_radius
                        && currPoint.y >= p.y - mDetection_radius && currPoint.y <= p.y + mDetection_radius) {

                    // Annotation on fist point is not allowed, by design
                    if (i == 0) {
                        return false;
                    }

                    // Show annotation point
                    if (tracePoint.annotation == null) {
                        tracePoint.annotation = new TraceAnnotation();
                        invalidate();
                    }

                    // Edit annotation point
                    promptInput(tracePoint);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Prompts annotation input.
     *
     * @param tracePoint
     */
    private void promptInput(final TracePoint tracePoint) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mAppContext);
        builder.setTitle(mAppContext.getString(R.string.annotate));

        // Set up edit text
        final EditText input = new EditText(mAppContext);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setLines(3);
        input.setSingleLine(false);
        input.setGravity(Gravity.LEFT | Gravity.TOP);

        // Set up edit text message
        String msg = tracePoint.annotation.msg;
        if (msg != null) {
            input.setText(msg);
            input.setSelection(msg.length());
        }
        builder.setView(input);

        // Cancel
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface arg0) {
                // Delete this annotation if it is invalid
                if (tracePoint.annotation.msg == null) {
                    tracePoint.annotation = null;
                }
                invalidate();
            }
        });

        // Delete
        builder.setNegativeButton(mAppContext.getText(R.string.delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tracePoint.annotation = null;
                invalidate();
            }
        });
        builder.setPositiveButton(mAppContext.getText(R.string.set), null);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // Positive button
        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = input.getText().toString();
                if (text.length() != 0) {
                    tracePoint.annotation.msg = text;
                    alertDialog.dismiss();
                    invalidate();
                } else {
                    TraceUtil.showToast(mAppContext, mAppContext.getString(R.string.toast_enter_annotation));
                }
            }
        });


    }
}

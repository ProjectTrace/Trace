package com.uw.hcde.fizzlab.trace.userInterface.drawing;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.utility.TraceUtil;
import com.uw.hcde.fizzlab.trace.dataContainer.TraceAnnotation;
import com.uw.hcde.fizzlab.trace.dataContainer.TracePoint;

import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

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
    private Context mContext;

    private float mCircle_radius_bg;
    private float mCircle_radius_small;
    private float mDetection_radius;

    public AnnotationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        mCircle_radius_bg = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                CIRCLE_RADIUS_BG_DP, getResources().getDisplayMetrics());
        mCircle_radius_small = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                CIRCLE_RADIUS_SMALL_DP, getResources().getDisplayMetrics());
        mDetection_radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                ANNOTATION_DETECTION_RADIUS_DP, getResources().getDisplayMetrics());

        // Set up paint
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(getResources().getColor(R.color.cyan_light));
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
        mPaint.setColor(getResources().getColor(R.color.cyan_light));
        canvas.drawCircle(p.x, p.y, mCircle_radius_bg, mPaint);
        mPaint.setColor(getResources().getColor(R.color.cyan));
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
        final MaterialDialog dialog = new MaterialDialog(mContext);

        //AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        dialog.setTitle(R.string.annotate);

        // Set up dialog view
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_annotate, null);
        final EditText input = (EditText) view.findViewById(R.id.annotate_content);

        // Set up edit text message
        String msg = tracePoint.annotation.msg;
        if (msg != null) {
            input.setText(msg);
            input.setSelection(msg.length());
        }
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);

        // Dismiss
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // Delete this annotation if it is invalid
                if (tracePoint.annotation.msg == null) {
                    tracePoint.annotation = null;
                }
                invalidate();
            }
        });

        dialog.setNegativeButton(R.string.delete, new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setPositiveButton(R.string.set, new OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = input.getText().toString();
                if (text.length() != 0) {
                    tracePoint.annotation.msg = text;
                    dialog.dismiss();
                    invalidate();
                } else {
                    TraceUtil.showToast(mContext, mContext.getString(R.string.toast_enter_annotation));
                }
            }
        });

        dialog.show();
    }
}

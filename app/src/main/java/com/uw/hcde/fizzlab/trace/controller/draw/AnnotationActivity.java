package com.uw.hcde.fizzlab.trace.controller.draw;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.controller.TraceUtil;
import com.uw.hcde.fizzlab.trace.controller.main.MainActivity;
import com.uw.hcde.fizzlab.trace.model.object.TracePoint;
import com.uw.hcde.fizzlab.trace.model.parse.ParseDataFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity that handles annotation. Drawing activity
 * and annotation activity are separated in current design.
 *
 * @author tianchi
 */
public class AnnotationActivity extends Activity {

    private static final String TAG = "AnnotateActivity";

    private List<Point> mRawPoints; // Used to display path
    private List<TracePoint> mTracePoints; // Trace points

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annotate);

        Intent intent = getIntent();
        mRawPoints = intent.getParcelableArrayListExtra(DrawActivity.INTENT_EXTRA_RAW_POINTS);

        // More pipelines can be added here
        List<Point> normalizedPoints = DrawUtil.normalizePoints(mRawPoints);

        // Get tracePoints
        mTracePoints = DrawUtil.pointsToTracePoints(normalizedPoints);

        Log.d(TAG, "raw points size: " + mRawPoints.size());
        Log.d(TAG, "trace points size: " + mTracePoints.size());

        // Set navigation title
        TextView title = (TextView) findViewById(R.id.navigation_title);
        title.setText(getString(R.string.annotate));

        // Set up drawing view path
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.drawing_view_path);
        layout.addView(new DrawingViewPath(this));

        final AnnotationView annotationView = (AnnotationView) findViewById(R.id.drawing_view_annotation);
        annotationView.setTracePoints(mTracePoints);

        setupButtons();
    }

    /**
     * Sets up done button
     */
    private void setupButtons() {
        // Set up buttons
        View buttonSend = findViewById(R.id.button_send);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button send clicked");

                // Send dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(AnnotationActivity.this);
                builder.setTitle(AnnotationActivity.this.getString(R.string.send));

                // Set up edit text
                final EditText input = new EditText(AnnotationActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setSingleLine(false);
                input.setLines(3);
                input.setGravity(Gravity.LEFT | Gravity.TOP);
                input.setHint(AnnotationActivity.this.getString(R.string.hint_enter_username));
                builder.setView(input);

                builder.setPositiveButton(AnnotationActivity.this.getText(R.string.ok), null);
                builder.setNegativeButton(AnnotationActivity.this.getText(R.string.cancel), null);

                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

                // Sets positive button
                Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Get list of username
                        List<String> names = new ArrayList<String>();
                        for (String s : input.getText().toString().split(",")) {
                            String name = s.trim();
                            if (name.length() > 0) {
                                names.add(name);
                            }
                        }

                        // Empty list
                        if (names.isEmpty()) {
                            TraceUtil.showToast(AnnotationActivity.this, getString(R.string.toast_enter_username));
                            return;
                        }

                        // TODO: handle invalid list
                        // TODO: more pipelines on tracePoints

                        // Send data
                        ParseDataFactory.sendDrawing(names, mTracePoints);

                        alertDialog.dismiss();
                        Intent intent = new Intent(AnnotationActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
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

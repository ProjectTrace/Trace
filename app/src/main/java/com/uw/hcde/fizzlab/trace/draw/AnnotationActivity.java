package com.uw.hcde.fizzlab.trace.draw;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.main.MainActivity;
import com.uw.hcde.fizzlab.trace.util.DrawUtil;

import java.util.ArrayList;

/**
 * Activity that handles annotation. Drawing activity
 * and annotation activity are separated in current design.
 *
 * @author tianchi
 */
public class AnnotationActivity extends Activity {

    private static final String TAG = "AnnotateActivity";

    private ArrayList<Point> mRawPoints; // Used to display path
    private ArrayList<Point> mTransformedPoints; // Used to add annotation point and upload to server

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annotate);

        Intent intent = getIntent();
        mRawPoints = intent.getParcelableArrayListExtra(DrawActivity.INTENT_EXTRA_RAW_POINTS);
        mTransformedPoints = DrawUtil.transformPointsBezierToDirect(mRawPoints);

        // Set navigation title
        TextView title = (TextView) findViewById(R.id.navigation_title);
        title.setText(getString(R.string.annotate));

        // Set up drawing view path
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.drawing_view_path);
        layout.addView(new DrawingViewPath(this));

        AnnotationView annotationView = (AnnotationView) findViewById(R.id.drawing_view_annotation);
        annotationView.setTransformedPoints(mTransformedPoints);

        // Set up buttons
        View buttonDone = findViewById(R.id.button_done);
        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button done clicked");
                Intent intent = new Intent(AnnotationActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Inner class to draw the path obtained from DrawingView activity.
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

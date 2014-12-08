package com.uw.hcde.fizzlab.trace.controller.map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.controller.draw.DrawUtil;
import com.uw.hcde.fizzlab.trace.model.object.TraceDataContainer;

import java.util.List;


/**
 * Shows drawing at receiver side.
 * TODO: need refactor on drawing view path.
 *
 * @author tianchi
 */
public class ShowDrawingActivity extends Activity {

    private static final String TAG = "ShowDrawingActivity";

    // Main buttons
    private View mButtonBack;
    private List<Point> mPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_drawing);

        // Set navigation title and back button
        TextView title = (TextView) findViewById(R.id.navigation_title);
        title.setText(getString(R.string.show_drawing));
        mButtonBack = findViewById(R.id.button_back);
        mButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mPoints = DrawUtil.tracePointsToPoints(TraceDataContainer.rawTracePoints);

        // Set up drawing view path
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.drawing_view_path);
        layout.addView(new DrawingViewPath(this));
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

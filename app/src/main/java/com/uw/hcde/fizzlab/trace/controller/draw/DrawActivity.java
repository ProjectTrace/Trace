package com.uw.hcde.fizzlab.trace.controller.draw;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.controller.main.MainActivity;

import java.util.ArrayList;

/**
 * Activity that handles drawing
 *
 * @author tianchi
 */
public class DrawActivity extends Activity {

    private static final String TAG = "DrawActivity";
    public static final String INTENT_EXTRA_RAW_POINTS = "raw_points";

    // Main buttons
    private View mButtonClear;
    private View mButtonNext;
    private View mButtonHome;
    private DrawingView mDrawingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        // Set navigation title
        TextView title = (TextView) findViewById(R.id.navigation_title);
        title.setText(getString(R.string.draw));

        // Set up buttons
        mButtonClear = findViewById(R.id.button_clear);
        mButtonNext = findViewById(R.id.button_next);
        mButtonHome = findViewById(R.id.navigation_button);
        setupListener();

        mDrawingView = (DrawingView) findViewById(R.id.drawing_view_annotation);
    }

    /**
     * Helper function to add button listeners
     */
    private void setupListener() {
        mButtonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button clear clicked");
                mDrawingView.clear();
            }
        });

        mButtonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DrawActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button next clicked");

                // Check if drawing is valid
                if (!mDrawingView.isValid()) {
                    mDrawingView.clear();
                    return;
                }

                ArrayList<Point> points = mDrawingView.getPoints();
                Intent intent = new Intent(DrawActivity.this, AnnotationActivity.class);
                intent.putParcelableArrayListExtra(INTENT_EXTRA_RAW_POINTS, points);
                startActivity(intent);
            }
        });
    }
}

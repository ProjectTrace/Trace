package com.uw.hcde.fizzlab.trace.draw;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.uw.hcde.fizzlab.trace.R;

/**
 * Activity that handles drawing
 *
 * @author tianchi
 */
public class DrawActivity extends Activity {

    private static final String TAG = "DrawActivity";

    // Main buttons
    private View mButtonClear;
    private View mButtonAnnotate;
    private DrawingView mDrawingView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        // Set navigation title
        TextView title = (TextView) findViewById(R.id.navigation_title);
        title.setText(getString(R.string.draw));

        mButtonClear = findViewById(R.id.button_clear);
        mButtonAnnotate = findViewById(R.id.button_annotate);
        mDrawingView = (DrawingView) findViewById(R.id.drawing_view);
        setupListener();
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

        mButtonAnnotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button annotate clicked");
                Intent intent = new Intent(DrawActivity.this, AnnotateActivity.class);
                startActivity(intent);
            }
        });
    }

}

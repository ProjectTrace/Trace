package com.uw.hcde.fizzlab.trace.draw;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.uw.hcde.fizzlab.trace.R;

public class DrawActivity extends Activity {

    private static final String TAG = "DrawActivity";

    // Three main buttons
    private View mButtonBack;
    private View mButtonClear;
    private View mButtonAnnotate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        mButtonBack = findViewById(R.id.main_button_draw);
        mButtonClear = findViewById(R.id.main_button_walk);
        mButtonAnnotate = findViewById(R.id.main_button_you);
    }

    /**
     * Helper function to add button listeners
     */
    private void setupListener() {
        mButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button back clicked");
            }
        });

        mButtonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button clear clicked");
            }
        });

        mButtonAnnotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button annotate clicked");
            }
        });
    }

}

package com.uw.hcde.fizzlab.trace;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.draw.DrawActivity;


/**
 * Application entry point
 */
public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    // Three main buttons
    private View mButtonDraw;
    private View mButtonWalk;
    private View mButtonYou;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonDraw = findViewById(R.id.main_button_draw);
        mButtonWalk = findViewById(R.id.main_button_walk);
        mButtonYou = findViewById(R.id.main_button_you);

        setupListener();
    }

    /**
     * Helper function to add button listeners
     */
    private void setupListener() {
        mButtonDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button draw clicked");
                Intent intent = new Intent(MainActivity.this, DrawActivity.class);
                startActivity(intent);
            }
        });

        mButtonWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button walk clicked");
            }
        });

        mButtonYou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button you clicked");
            }
        });
    }

}

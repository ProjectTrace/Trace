package com.uw.hcde.fizzlab.trace.controller.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.controller.TraceUtil;
import com.uw.hcde.fizzlab.trace.controller.draw.DrawActivity;
import com.uw.hcde.fizzlab.trace.controller.profile.ProfileActivity;
import com.uw.hcde.fizzlab.trace.controller.walk.ChooseDrawingActivity;


/**
 * Main application screen
 *
 * @author tianchi
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

        mButtonDraw = findViewById(R.id.button_draw);
        mButtonWalk = findViewById(R.id.button_walk);
        mButtonYou = findViewById(R.id.button_you);
        setupListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Checks if network connection is available
        TraceUtil.checkNetworkStatus(this);
    }

    /**
     * Helper function to add button listeners.
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
                Intent intent = new Intent(MainActivity.this, ChooseDrawingActivity.class);
                startActivity(intent);
            }
        });

        mButtonYou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button you clicked");
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }
}

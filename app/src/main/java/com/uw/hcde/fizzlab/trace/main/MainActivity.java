package com.uw.hcde.fizzlab.trace.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.ui.TermOfServiceActivity;
import com.uw.hcde.fizzlab.trace.ui.TermOfServiceFragment;
import com.uw.hcde.fizzlab.trace.utility.TraceAppPreference;
import com.uw.hcde.fizzlab.trace.utility.TraceUtil;
import com.uw.hcde.fizzlab.trace.ui.drawing.DrawActivity;
import com.uw.hcde.fizzlab.trace.ui.profile.ProfileActivity;
import com.uw.hcde.fizzlab.trace.ui.walking.ChooseWalkActivity;


/**
 * Main application screen.
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

        if (!TraceAppPreference.isTermAccepted(this)) {
            Log.d(TAG, "Show term of service");
            Intent intent = new Intent(this, TermOfServiceActivity.class);
            startActivity(intent);
        }
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
                Intent intent = new Intent(MainActivity.this, ChooseWalkActivity.class);
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

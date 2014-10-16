package com.uw.hcde.fizzlab.trace.profile;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.uw.hcde.fizzlab.trace.R;

public class ProfileActivity extends Activity {

    private static final String TAG = "ProfileActivity";

    // Buttons
    private View mButtonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setupListener();
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
    }
}

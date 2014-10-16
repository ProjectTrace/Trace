package com.uw.hcde.fizzlab.trace.walk;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.uw.hcde.fizzlab.trace.R;

public class WalkActivity extends Activity {

    private static final String TAG = "WalkActivity";

    // Buttons
    private Button mButtonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk);
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

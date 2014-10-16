package com.uw.hcde.fizzlab.trace.draw;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.uw.hcde.fizzlab.trace.R;

public class AnnotateActivity extends Activity {

    private static final String TAG = "AnnotateActivity";

    private View mButtonDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annotate);

        // Set navigation title
        TextView title = (TextView) findViewById(R.id.navigation_title);
        title.setText(getString(R.string.annotate));

        mButtonDone = findViewById(R.id.button_done);
        setupListener();
    }

    /**
     * Helper function to add button listeners
     */
    private void setupListener() {
        mButtonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button clear clicked");
            }
        });
    }
}

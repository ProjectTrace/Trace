package com.uw.hcde.fizzlab.trace.walk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.uw.hcde.fizzlab.trace.R;

public class ChooseDistanceActivity extends Activity {

    private static final String TAG = "ChooseDistanceActivity";

    private View mButtonNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_distance);

        // Set navigation title
        TextView title = (TextView) findViewById(R.id.navigation_title);
        title.setText(getString(R.string.choose_distance));

        // Set up button
        mButtonNext = findViewById(R.id.button_next);
        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button next clicked");
                Intent intent = new Intent(ChooseDistanceActivity.this, ChooseDrawingActivity.class);
                startActivity(intent);
            }
        });
    }
}

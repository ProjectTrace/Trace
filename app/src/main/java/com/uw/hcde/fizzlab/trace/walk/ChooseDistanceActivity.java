package com.uw.hcde.fizzlab.trace.walk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.uw.hcde.fizzlab.trace.R;

/**
 * Choose walking distance
 *
 * @author tianchi
 */
public class ChooseDistanceActivity extends Activity {

    private static final String TAG = "ChooseDistanceActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_distance);

        // Set navigation title
        TextView title = (TextView) findViewById(R.id.navigation_title);
        title.setText(getString(R.string.choose_distance));

        // Set up button
        View buttonNext = findViewById(R.id.button_next);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button next clicked");
                Intent intent = new Intent(ChooseDistanceActivity.this, ChooseDrawingActivity.class);
                startActivity(intent);
            }
        });
    }
}

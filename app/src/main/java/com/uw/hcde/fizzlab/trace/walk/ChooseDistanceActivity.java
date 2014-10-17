package com.uw.hcde.fizzlab.trace.walk;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.uw.hcde.fizzlab.trace.R;

public class ChooseDistanceActivity extends Activity {

    private static final String TAG = "WalkActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_distance);

        // Set navigation title
        TextView title = (TextView) findViewById(R.id.navigation_title);
        title.setText(getString(R.string.choose_distance));
    }
}

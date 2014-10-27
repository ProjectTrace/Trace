package com.uw.hcde.fizzlab.trace.draw;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.main.MainActivity;

/**
 * Activity that handles annotation
 *
 * @author tianchi
 */
public class AnnotateActivity extends Activity {

    private static final String TAG = "AnnotateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annotate);

        // Set navigation title
        TextView title = (TextView) findViewById(R.id.navigation_title);
        title.setText(getString(R.string.annotate));

        View buttonDone = findViewById(R.id.button_done);
        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button done clicked");
                Intent intent = new Intent(AnnotateActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}

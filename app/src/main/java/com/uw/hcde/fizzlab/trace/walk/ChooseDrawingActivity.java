package com.uw.hcde.fizzlab.trace.walk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.uw.hcde.fizzlab.trace.R;

/**
 * Choose drawing
 *
 * @author tianchi
 */
public class ChooseDrawingActivity extends Activity {

    private static final String TAG = "ChooseDrawingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_drawing);

        // Set navigation title
        TextView title = (TextView) findViewById(R.id.navigation_title);
        title.setText(getString(R.string.choose_drawing));

        // Go button
        View buttonGo = findViewById(R.id.button_go);
        buttonGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button go clicked");
                Intent intent = new Intent(ChooseDrawingActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });
    }
}

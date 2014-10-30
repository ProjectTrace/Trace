package com.uw.hcde.fizzlab.trace.walk;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.main.MainActivity;

public class MapActivity extends Activity{
    private static final String TAG = "MapActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Set navigation title
        TextView title = (TextView) findViewById(R.id.navigation_title);
        title.setText(getString(R.string.map));

        View buttonDone = findViewById(R.id.button_done);
        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button done clicked");
                Intent intent = new Intent(MapActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}

package com.uw.hcde.fizzlab.trace.module;

import android.app.Activity;
import android.os.Bundle;

import com.uw.hcde.fizzlab.trace.R;


/**
 * Application entry point
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}

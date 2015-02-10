package com.uw.hcde.fizzlab.trace.main;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;
import com.uw.hcde.fizzlab.trace.database.ParseAnnotation;
import com.uw.hcde.fizzlab.trace.database.ParseDrawing;

/**
 * Configures Parse.
 *
 * @author tianchi, Ethan
 */
public class TraceApplication extends Application {

    private static final String TAG = "TraceApplication";
    private static final String PARSE_APP_ID = "7QgrBfPkXxcgHJKSaTTVcaiQHVJV5OxV84YdRrCC";
    private static final String PARSE_CLIENT_KEY = "6ohugWbOCsePh0QO7fC1w5ro428kupfBw7Q1k0Kz";

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(ParseDrawing.class);
        ParseObject.registerSubclass(ParseAnnotation.class);
        Parse.initialize(this, PARSE_APP_ID, PARSE_CLIENT_KEY);

    }
}
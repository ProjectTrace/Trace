package com.uw.hcde.fizzlab.trace.main;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.SaveCallback;
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

        //Broadcast channel for notification
        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });
    }
}
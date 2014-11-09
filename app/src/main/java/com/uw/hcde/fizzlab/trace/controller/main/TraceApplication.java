package com.uw.hcde.fizzlab.trace.controller.main;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.parse.Parse;
import com.parse.ParseObject;
import com.uw.hcde.fizzlab.trace.model.parse.ParseAnnotation;
import com.uw.hcde.fizzlab.trace.model.parse.ParseDrawing;

/**
 * Configures Parse
 *
 * @author tianchi
 */
public class TraceApplication extends Application {

    private static final String TAG = "TraceApplication";
    private static final String PARSE_APP_ID = "7QgrBfPkXxcgHJKSaTTVcaiQHVJV5OxV84YdRrCC";
    private static final String PARSE_CLIENT_KEY = "6ohugWbOCsePh0QO7fC1w5ro428kupfBw7Q1k0Kz";
    private static final String APP_ID = "com.uw.hcde.fizzlab.trace";

    private static SharedPreferences preferences;

    // Key for sharedPreference
    //private static final String KEY = "key";

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(ParseDrawing.class);
        ParseObject.registerSubclass(ParseAnnotation.class);
        Parse.initialize(this, PARSE_APP_ID, PARSE_CLIENT_KEY);
        preferences = getSharedPreferences(APP_ID, Context.MODE_PRIVATE);
    }

//    public static float getKey() {
//        return preferences.getInt(KEY, DEFAULT_VALUE);
//    }
//
//    public static void setKey(int value) {
//        preferences.edit().putFloat(KEY, value).commit();
//    }

}
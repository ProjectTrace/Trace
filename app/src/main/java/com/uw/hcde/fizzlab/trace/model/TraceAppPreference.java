package com.uw.hcde.fizzlab.trace.model;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * App preferences.
 *
 * @author tianchi
 */
public class TraceAppPreference {
    private static final String APP_ID = "com.uw.hcde.fizzlab.trace";
    private static SharedPreferences preferences = null;

    private static final String KEY = "sample key";

    /**
     * Lazy initialization.
     *
     * @param context
     */
    private static void initializePreferences(Context context) {
        if (preferences == null) {
            preferences = context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE);
        }
    }

    public static float getKey(Context context) {
        initializePreferences(context);
        return preferences.getInt(KEY, 0);
    }

    public static void setKey(Context context, int value) {
        initializePreferences(context);
        preferences.edit().putFloat(KEY, value).commit();
    }
}

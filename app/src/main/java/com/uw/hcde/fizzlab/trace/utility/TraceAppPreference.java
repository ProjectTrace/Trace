package com.uw.hcde.fizzlab.trace.utility;

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

    private static final String KEY_FIRST_TIME_USE = "sample key";

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

    /**
     * Is first time use the app. Set it false after called.
     * @param context
     * @return
     */
    public static boolean isFirstTimeUse(Context context) {
        initializePreferences(context);
        boolean res = preferences.getBoolean(KEY_FIRST_TIME_USE, true);
        if (res) {
            preferences.edit().putBoolean(KEY_FIRST_TIME_USE, false).commit();
        }
        return res;
    }


//    public static void setKey(Context context, int value) {
//        initializePreferences(context);
//        preferences.edit().putFloat(KEY, value).commit();
//    }
}

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

    private static final String KEY_FIRST_TIME_USE = "key_first_time_use";
    private static final String KEY_IS_TERM_ACCEPTED = "key_is_term_accepted";

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

    public static boolean isTermAccepted(Context context) {
        initializePreferences(context);
        return preferences.getBoolean(KEY_IS_TERM_ACCEPTED, false);
    }

    public static void setTermAccepted(Context context) {
        initializePreferences(context);
        preferences.edit().putBoolean(KEY_IS_TERM_ACCEPTED, true).commit();
    }

}

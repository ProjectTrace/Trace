package com.uw.hcde.fizzlab.trace.main;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;
import com.uw.hcde.fizzlab.trace.database.ParseAnnotation;
import com.uw.hcde.fizzlab.trace.database.ParseDrawing;
import com.uw.hcde.fizzlab.trace.database.ParseLog;

/**
 * Configures Parse.
 *
 * @author tianchi, Ethan
 */
public class TraceApplication extends Application {

    private static final String TAG = "TraceApplication";
    private static final String PARSE_APP_ID = "YiZKuDsei4bL3jJzPUXyE8T9yywpm8UzTE8JCRow";
    private static final String PARSE_CLIENT_KEY = "2l5Fln20bcBdaYHH6NJ4AoC7pO41yCP3S7c4gPel";

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(ParseDrawing.class);
        ParseObject.registerSubclass(ParseAnnotation.class);
        ParseObject.registerSubclass(ParseLog.class);
        Parse.initialize(this, PARSE_APP_ID, PARSE_CLIENT_KEY);

    }
}
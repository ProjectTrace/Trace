package com.uw.hcde.fizzlab.trace.main;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseObject;
import com.uw.hcde.fizzlab.trace.database.ParseAnnotation;
import com.uw.hcde.fizzlab.trace.database.ParseDrawing;
import com.uw.hcde.fizzlab.trace.database.ParseLog;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
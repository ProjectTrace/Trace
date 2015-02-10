package com.uw.hcde.fizzlab.trace.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseUser;
import com.uw.hcde.fizzlab.trace.ui.welcome.WelcomeActivity;

/**
 * Activity which starts an intent for either Welcome activity
 * or Main activity.
 *
 * @author tianchi
 */
public class DispatchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Dispatch activity according to parse status
        if (ParseUser.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity(new Intent(this, WelcomeActivity.class));
        }
         
    }
}

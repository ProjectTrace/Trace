package com.uw.hcde.fizzlab.trace.main;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.utility.TraceUtil;

/**
 * Welcome activity that contains sign in and sign up fragments.
 *
 * @author tianchi
 */
public class WelcomeActivity extends Activity {

    private static final String TAG = "WelcomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Fragment fragment = new SignInFragment();
        getFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Checks if network connection is available
        TraceUtil.checkNetworkStatus(this);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

}

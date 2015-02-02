package com.uw.hcde.fizzlab.trace.userInterface.profile;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;

import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.userInterface.TraceBaseActivity;

/**
 * Profile activity.
 *
 * @author tianchi
 */
public class ProfileActivity extends TraceBaseActivity {

    private static final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNavigationBarType(TraceBaseActivity.NAVIGATION_BAR_TYPE_PROFILE);

        Fragment fragment = new ProfileFragment();
        getFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
    }
}

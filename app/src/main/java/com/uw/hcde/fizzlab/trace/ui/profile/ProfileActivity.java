package com.uw.hcde.fizzlab.trace.ui.profile;

import android.app.Fragment;
import android.os.Bundle;

import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.ui.BaseActivity;

/**
 * Profile activity.
 *
 * @author tianchi
 */
public class ProfileActivity extends BaseActivity {

    private static final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNavigationBarType(BaseActivity.NAVIGATION_BAR_TYPE_PROFILE);

        Fragment fragment = new ProfileFragment();
        getFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
    }
}

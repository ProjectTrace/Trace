package com.uw.hcde.fizzlab.trace.userInterface.drawing;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.userInterface.BaseActivity;

/**
 * Activity that handles drawing and annotation.
 *
 * @author tianchi
 */
public class DrawActivity extends BaseActivity {

    private static final String TAG = "DrawActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNavigationBarType(BaseActivity.NAVIGATION_BAR_TYPE_DRAW);

        Fragment fragment = new DrawFragment();
        getFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
    }

    @Override
    protected void handleBackButton() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
            setNavigationTitle(R.string.draw);

        } else {
            super.handleBackButton();
        }
    }
}

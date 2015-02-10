package com.uw.hcde.fizzlab.trace.ui.walking;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.ui.BaseActivity;

/**
 * Choose walk activity that controls choose drawing and distance fragments.
 *
 * @author tianchi
 */
public class ChooseWalkActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setNavigationBarType(BaseActivity.NAVIGATION_BAR_TYPE_CYAN);
        enableReportButton();
        enableHomeButton();

        Fragment fragment = new ChooseDrawingFragment();
        getFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
    }

    @Override
    protected void handleBackButton() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
            setNavigationTitle(R.string.choose_drawing);
        } else {
            super.handleBackButton();
        }
    }
}

package com.uw.hcde.fizzlab.trace.ui.walking;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.ui.BaseActivity;
import com.uw.hcde.fizzlab.trace.utility.TraceUtil;

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

        //track statistics about each app opened
        ParseAnalytics.trackAppOpened(getIntent());
        ParseInstallation.getCurrentInstallation().saveInBackground();

        Fragment fragment = new ChooseDrawingFragment();
        getFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
    }

    @Override
    protected void handleBackButton() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
            setNavigationTitle(R.string.walk_step_1);
        } else {
            super.handleBackButton();
        }
    }


    @Override
    protected void handleReportButton() {
        FragmentManager fm = getFragmentManager();
        // On draw fragment
        if (fm.getBackStackEntryCount() == 0) {
            TraceUtil.showTutorialDialog(this, R.string.select_drawing_instruction);
        } else {
            TraceUtil.showTutorialDialog(this, R.string.set_duration_instruction);
        }
    }
}

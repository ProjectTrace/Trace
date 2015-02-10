package com.uw.hcde.fizzlab.trace.ui.walking;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
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
<<<<<<< HEAD:app/src/main/java/com/uw/hcde/fizzlab/trace/userInterface/walking/ChooseWalkActivity.java
        setNavigationBarType(BaseActivity.NAVIGATION_BAR_TYPE_WALK);
        //track statistics about each app opened
        ParseAnalytics.trackAppOpened(getIntent());
        ParseInstallation.getCurrentInstallation().saveInBackground();
=======

        setNavigationBarType(BaseActivity.NAVIGATION_BAR_TYPE_CYAN);
        enableReportButton();
        enableHomeButton();
>>>>>>> eab5045605ab723033be0ca50c2f222a652f6d59:app/src/main/java/com/uw/hcde/fizzlab/trace/ui/walking/ChooseWalkActivity.java

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

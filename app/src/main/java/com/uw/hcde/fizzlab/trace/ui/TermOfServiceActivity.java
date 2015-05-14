package com.uw.hcde.fizzlab.trace.ui;

import android.app.Fragment;
import android.os.Bundle;

import com.uw.hcde.fizzlab.trace.R;

public class TermOfServiceActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setNavigationBarType(BaseActivity.NAVIGATION_BAR_TYPE_CYAN);
        disableAllButtons();

        Fragment fragment = new TermOfServiceFragment();
        getFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        //STAY AT THIS SCREEN
    }
}

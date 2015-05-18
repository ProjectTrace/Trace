package com.uw.hcde.fizzlab.trace.ui.welcome;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.login.widget.LoginButton;
import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.ui.BaseActivity;


/**
 * Welcome activity that contains sign in and sign up fragments.
 *
 * @author tianchi
 */
public class WelcomeActivity extends BaseActivity {

    private static final String TAG = "WelcomeActivity";
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setNavigationBarType(BaseActivity.NAVIGATION_BAR_TYPE_CYAN);
        disableNavigationBar();

        fragment = new SignInFragment();
        getFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
    }

    @Override
    public void handleBackButton() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
            disableNavigationBar();
        } else {
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

}

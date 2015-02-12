package com.uw.hcde.fizzlab.trace.ui.drawing;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;

import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.ui.BaseActivity;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Activity that handles drawing and annotation.
 *
 * @author tianchi
 */
public class DrawActivity extends BaseActivity {

    private static final String TAG = "DrawActivity";

    // Use tag to identify fragments in the future.
    public static final String DRAW_FRAGMENT_TAG = "draw_fragment_tag";
    public static final String ANNOTATION_FRAGMENT_TAG = "annotation_fragment_tag";
    public static final String SELECT_FRIEND_FRAGMENT_TAG = "select_friend_fragment_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setNavigationBarType(BaseActivity.NAVIGATION_BAR_TYPE_ROSE);
        enableHomeButton();
        enableReportButton();

        Fragment fragment = new DrawFragment();
        getFragmentManager().beginTransaction().add(R.id.fragment_container, fragment, DRAW_FRAGMENT_TAG).commit();
    }

    @Override
    protected void handleBackButton() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() == 1) {
            fm.popBackStack();
            setNavigationTitle(R.string.draw_step_1);

        } else if (fm.getBackStackEntryCount() == 2) {
            fm.popBackStack();
            setNavigationTitle(R.string.draw_step_2);

        } else {
            super.handleBackButton();
        }
    }

    @Override
    protected void handleReportButton() {
        FragmentManager fm = getFragmentManager();

        // On draw fragment
        if (fm.getBackStackEntryCount() == 0) {

            final MaterialDialog dialog = new MaterialDialog(DrawActivity.this);
            dialog.setTitle(R.string.tutorial);
            dialog.setMessage(R.string.draw_instruction);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.setPositiveButton(R.string.report_problem, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    sendReport();
                }
            });
            dialog.show();

        } else {
            sendReport();
        }
    }

    private void sendReport() {
        super.handleReportButton();
    }
}

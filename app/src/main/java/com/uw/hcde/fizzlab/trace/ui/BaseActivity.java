package com.uw.hcde.fizzlab.trace.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.main.MainActivity;
import com.uw.hcde.fizzlab.trace.utility.TraceUtil;

/**
 * Base activity.
 *
 * @author tianchi
 */
public abstract class BaseActivity extends Activity {
    public static final String TAG = "TraceBaseActivity";
    public static final int NAVIGATION_BAR_TYPE_CYAN = 1;
    public static final int NAVIGATION_BAR_TYPE_ROSE = 2;

    ViewGroup mNavigationBar;
    TextView mNavigationTitle;
    View mNavigationDivider;

    ImageView mButtonHome;
    View mButtonBack;
    ImageView mButtonReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trace_base);

        mNavigationBar = (ViewGroup) findViewById(R.id.navigation_content);
        mNavigationTitle = (TextView) findViewById(R.id.navigation_title);
        mNavigationDivider = findViewById(R.id.navigation_divider);

        mButtonHome = (ImageView) findViewById(R.id.navigation_button_home);
        mButtonBack = findViewById(R.id.navigation_button_back);
        mButtonReport = (ImageView) findViewById(R.id.navigation_button_report);
        setupListener();
    }

    private void setupListener() {
        mButtonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        mButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBackButton();
            }
        });

        mButtonReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleReportButton();
            }
        });
    }

    protected void handleReportButton() {
        TraceUtil.showReportDialog(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Checks if network connection is available
        TraceUtil.checkNetworkStatus(this);
    }

    @Override
    public void onBackPressed() {
        handleBackButton();
    }

    protected void handleBackButton() {
        finish();
    }

    public void enableHomeButton() {
        mButtonHome.setVisibility(View.VISIBLE);
        mButtonBack.setVisibility(View.INVISIBLE);
    }

    public void disableAllButtons() {
        mButtonReport.setVisibility(View.INVISIBLE);
        mButtonHome.setVisibility(View.INVISIBLE);
        mButtonBack.setVisibility(View.INVISIBLE);
        mButtonBack.setVisibility(View.INVISIBLE);
    }

    public void enableBackButton() {
        mButtonBack.setVisibility(View.VISIBLE);
        mButtonHome.setVisibility(View.INVISIBLE);
    }

    public void enableReportButton() {
        mButtonReport.setVisibility(View.VISIBLE);
    }

    public void disableReportButton() {
        mButtonReport.setVisibility(View.INVISIBLE);
    }

    public void enableNavigationBar() {
        mNavigationTitle.setVisibility(View.VISIBLE);
        mNavigationDivider.setVisibility(View.VISIBLE);
    }

    public void disableNavigationBar() {
        for (int i = 0; i < mNavigationBar.getChildCount(); i++) {
            mNavigationBar.getChildAt(i).setVisibility(View.INVISIBLE);
        }
    }


    /**
     * Sets navigation color
     *
     * @param type
     */
    public void setNavigationBarType(int type) {

        if (type == NAVIGATION_BAR_TYPE_ROSE) {
            mNavigationBar.setBackgroundColor(getResources().getColor(R.color.rose_navigation_title));
            mButtonReport.setImageResource(R.drawable.report_rose);
            mButtonHome.setImageResource(R.drawable.home_rose);

        } else {
            mNavigationBar.setBackgroundColor(getResources().getColor(R.color.cyan_navigation_title));
            mButtonReport.setImageResource(R.drawable.report_cyan);
            mButtonHome.setImageResource(R.drawable.home_cyan);
        }
    }

    /**
     * Sets navigation title
     */
    public void setNavigationTitle(int resId) {
        mNavigationTitle.setText(getString(resId));
    }
}

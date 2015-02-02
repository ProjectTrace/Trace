package com.uw.hcde.fizzlab.trace.userInterface;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.main.MainActivity;
import com.uw.hcde.fizzlab.trace.utility.TraceUtil;

import info.hoang8f.widget.FButton;

/**
 * Base activity.
 *
 * @author tianchi
 */
public abstract class TraceBaseActivity extends Activity {
    public static final String TAG = "TraceBaseActivity";
    public static final int NAVIGATION_BAR_TYPE_DRAW = 1;
    public static final int NAVIGATION_BAR_TYPE_WALK = 2;
    public static final int NAVIGATION_BAR_TYPE_PROFILE = 3;
    public static final int NAVIGATION_BAR_TITLE_SIZE_SMALL_SP = 17;
    public static final int NAVIGATION_BAR_TITLE_SIZE_SP = 22;

    View mButtonHome;
    View mButtonReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trace_base);

        mButtonHome = findViewById(R.id.navigation_button_home);
        mButtonReport = findViewById(R.id.navigation_button_report);
        setupListener();
    }

    private void setupListener() {
        mButtonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TraceBaseActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        mButtonReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TraceUtil.showToast(TraceBaseActivity.this, "report a problem");
            }
        });
    }

    /**
     * Sets navigation color
     *
     * @param type
     */
    public void setNavigationBarType(int type) {
        View bar = findViewById(R.id.navigation_content);
        FButton button = (FButton) findViewById(R.id.navigation_button_report);

        if (type == NAVIGATION_BAR_TYPE_DRAW) {
            bar.setBackgroundColor(getResources().getColor(R.color.rose));
            button.setButtonColor(getResources().getColor(R.color.rose_dark));
        } else {
            bar.setBackgroundColor(getResources().getColor(R.color.cyan));
            button.setButtonColor(getResources().getColor(R.color.cyan_dark));
        }
    }

    /**
     * Sets navigation title
     */
    public void setNavigationTitle(int resId) {
        setNavigationTitle(resId, NAVIGATION_BAR_TITLE_SIZE_SP);
    }

    /**
     * Sets navigation title
     */
    public void setNavigationTitle(int resId, float sizeSp) {
        TextView title = (TextView) findViewById(R.id.navigation_title);
        title.setText(getString(resId));
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeSp);
    }
}

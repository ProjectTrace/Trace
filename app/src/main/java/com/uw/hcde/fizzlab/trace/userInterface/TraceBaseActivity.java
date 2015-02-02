package com.uw.hcde.fizzlab.trace.userInterface;

import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.uw.hcde.fizzlab.trace.R;

/**
 * Base activity.
 */
public abstract class TraceBaseActivity extends Activity{
    View mButtonHome;
    View mButtonReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trace_base);

        mButtonHome = findViewById(R.id.navigation_button_home);

    }

    /**
     * Sets navigation color
     * @param resId
     */
    public void setNavigationBackground(int resId) {
        View view = findViewById(R.id.navigation_content);
        view.setBackground(getResources().getDrawable(resId));
    }

    /**
     * Sets navigation title
     */
    public void setNavigationTitle(int resId) {
        TextView title = (TextView) findViewById(R.id.navigation_title);
        title.setText(getString(resId));
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

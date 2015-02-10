package com.uw.hcde.fizzlab.trace.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.main.MainActivity;
import com.uw.hcde.fizzlab.trace.utility.TraceUtil;

import info.hoang8f.widget.FButton;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * Base activity.
 *
 * @author tianchi
 */
public abstract class BaseActivity extends Activity {
    public static final String TAG = "TraceBaseActivity";
    public static final int NAVIGATION_BAR_TYPE_DRAW = 1;
    public static final int NAVIGATION_BAR_TYPE_WALK = 2;
    public static final int NAVIGATION_BAR_TYPE_PROFILE = 3;
    public static final int NAVIGATION_BAR_TITLE_SIZE_SMALL_SP = 17;
    public static final int NAVIGATION_BAR_TITLE_SIZE_SP = 22;

    View mButtonHome;
    View mButtonBack;
    View mButtonReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trace_base);

        mButtonHome = findViewById(R.id.navigation_button_home);
        mButtonBack = findViewById(R.id.navigation_button_back);
        mButtonReport = findViewById(R.id.navigation_button_report);
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
                final MaterialDialog dialog = new MaterialDialog(BaseActivity.this);
                dialog.setTitle(R.string.report_problem);

                // Set up dialog view
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.dialog_report_problem, null);
                final EditText input = (EditText) view.findViewById(R.id.report_content);
                dialog.setContentView(view);
                dialog.setCanceledOnTouchOutside(true);

                dialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.setPositiveButton(R.string.send, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String report = input.getText().toString();
                        if (report.length() != 0) {
                            // Send to database
                        }
                        dialog.dismiss();
                        TraceUtil.showToast(BaseActivity.this, getString(R.string.thanks));
                    }
                });
                dialog.show();
            }
        });
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

    public void enableBackButton() {
        mButtonBack.setVisibility(View.VISIBLE);
        mButtonHome.setVisibility(View.INVISIBLE);
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

package com.uw.hcde.fizzlab.trace.controller.walk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.controller.map.MapActivity;
import com.uw.hcde.fizzlab.trace.model.object.TraceDataContainer;

import java.lang.reflect.Field;

/**
 * Choose walking duration
 *
 * @author tianchi
 */
public class ChooseDurationActivity extends Activity {

    private static final String TAG = "ChooseDistanceActivity";

    // Average speeds
    private static final int AVERAGE_SPEED_METER_PER_MINUTE = 80;
    private static final int sDurations[] = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120,
            130, 140, 150, 160, 170, 180, 190, 200};

    private View mButtonGo;
    private View mButtonBack;
    private NumberPicker mPickerDistance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_duration);

        // Set navigation title
        TextView title = (TextView) findViewById(R.id.navigation_title);
        title.setText(getString(R.string.choose_duration));

        mPickerDistance = (NumberPicker) findViewById(R.id.picker_duration);
        initializePicker();

        // Buttons
        mButtonGo = findViewById(R.id.button_go);
        mButtonBack = findViewById(R.id.button_back);
        setupButtons();
    }

    /**
     * Sets up buttons
     */
    private void setupButtons() {
        mButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mButtonGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button go clicked");

                // Check GPS service
                LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
                boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (!enabled) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChooseDurationActivity.this);
                    builder.setTitle(getString(R.string.location_service_disabled));

                    // Sets up message window
                    TextView text = new TextView(ChooseDurationActivity.this);
                    text.setText(getString(R.string.enable_gps));
                    text.setPadding(20, 20, 0, 20);
                    builder.setView(text);

                    // Negative button
                    builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Nothing
                        }
                    });

                    // Positive button
                    builder.setPositiveButton(getString(R.string.settings), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    });
                    builder.create().show();
                    return;
                }

                TraceDataContainer.distance = AVERAGE_SPEED_METER_PER_MINUTE * sDurations[mPickerDistance.getValue()];
                Log.d(TAG, "Distance : " + TraceDataContainer.distance);
                Intent intent = new Intent(ChooseDurationActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Sets up distance picker
     */
    private void initializePicker() {
        mPickerDistance.setMaxValue(sDurations.length - 1);
        mPickerDistance.setMinValue(0);
        mPickerDistance.setWrapSelectorWheel(false);
        mPickerDistance.setValue(2);
        mPickerDistance.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        String displayed[] = new String[sDurations.length];
        for (int i = 0; i < displayed.length; i++) {
            displayed[i] = formatDouble(sDurations[i]) + " mins";
        }
        mPickerDistance.setDisplayedValues(displayed);

        // Change divider color, bad way
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    pf.set(mPickerDistance, getResources().getDrawable(
                            R.drawable.numberpicker_selection_divider));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    /**
     * Helper method for formatting string
     */
    private String formatDouble(double d) {
        int i = (int) d;
        return d == i ? String.valueOf(i) : String.valueOf(d);
    }
}

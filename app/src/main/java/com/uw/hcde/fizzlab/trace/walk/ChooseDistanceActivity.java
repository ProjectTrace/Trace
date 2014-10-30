package com.uw.hcde.fizzlab.trace.walk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.uw.hcde.fizzlab.trace.R;

import java.lang.reflect.Field;

/**
 * Choose walking distance
 *
 * @author tianchi
 */
public class ChooseDistanceActivity extends Activity {

    public static final String TAG = "ChooseDistanceActivity";

    //Choices of distances in miles
    private static final double sDistances[] = {0.5, 1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5,
            5, 5.5, 6, 6.5, 7, 7.5, 8, 8.5, 9, 9.5, 10};
    public static final String Extra_Distance_Index = "extra_distance_index";

    private NumberPicker mPickerDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_distance);

        // Set navigation title
        TextView title = (TextView) findViewById(R.id.navigation_title);
        title.setText(getString(R.string.choose_distance));

        mPickerDistance = (NumberPicker) findViewById(R.id.picker_distance);
        initializePicker();

        // Set up Next button
        View buttonNext = findViewById(R.id.button_next);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button next clicked");
                Log.d(TAG, "Distance index: " + mPickerDistance.getValue());

                Intent intent = new Intent(ChooseDistanceActivity.this, ChooseDrawingActivity.class);
                intent.putExtra(Extra_Distance_Index, mPickerDistance.getValue());
                startActivity(intent);
            }
        });
    }

    /**
     * Sets up distance picker
     */
    private void initializePicker() {
        mPickerDistance.setMaxValue(sDistances.length - 1);
        mPickerDistance.setMinValue(0);
        mPickerDistance.setWrapSelectorWheel(false);
        mPickerDistance.setValue(1);
        mPickerDistance.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        String displayed[] = new String[sDistances.length];
        for (int i = 0; i < displayed.length; i++) {
            displayed[i] = formatDouble(sDistances[i]);
            if (i > 1) {
                displayed[i] += " miles";
            } else {
                displayed[i] += " mile";
            }
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

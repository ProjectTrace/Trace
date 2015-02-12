package com.uw.hcde.fizzlab.trace.ui.walking;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.dataContainer.TraceDataContainerReceiver;
import com.uw.hcde.fizzlab.trace.navigation.MapActivity;
import com.uw.hcde.fizzlab.trace.ui.BaseActivity;
import com.uw.hcde.fizzlab.trace.utility.TraceUtil;

import java.lang.reflect.Field;

/**
 * Choose walking duration fragment.
 *
 * @author tianchi
 */
public class ChooseDurationFragment extends Fragment {

    private static final String TAG = "ChooseDurationFragment";

    // Average speeds
    private static final int AVERAGE_SPEED_METER_PER_MINUTE = 80;
    private static final int sDurations[] = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120,
            130, 140, 150, 160, 170, 180, 190, 200};

    private View mButtonGo;
    private NumberPicker mPickerDistance;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_duration, container, false);

        // Set navigation title
        ((BaseActivity) getActivity()).setNavigationTitle(R.string.choose_duration);

        mPickerDistance = (NumberPicker) view.findViewById(R.id.picker_duration);
        initializePicker();

        // Buttons
        mButtonGo = view.findViewById(R.id.button_go);
        setupButtons();

        return view;
    }

    /**
     * Sets up buttons
     */
    private void setupButtons() {
        mButtonGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button go clicked");

                // Check connection, gps status and google play status
                if (!TraceUtil.checkGPSStatus(getActivity())
                        || !TraceUtil.checkNetworkStatus(getActivity())
                        || !TraceUtil.checkGooglePlayService(getActivity())) {
                    return;
                }

                TraceDataContainerReceiver.distance = AVERAGE_SPEED_METER_PER_MINUTE * sDurations[mPickerDistance.getValue()];
                Log.d(TAG, "Distance : " + TraceDataContainerReceiver.distance);
                Intent intent = new Intent(getActivity(), MapActivity.class);
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

        // Change divider color and text color, bad way
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
            } else if (pf.getName().equals("mSelectorWheelPaint")) {
                pf.setAccessible(true);
                try {
                    ((Paint) pf.get(mPickerDistance)).setColor(getResources().getColor(R.color.white));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (pf.getName().equals("mInputText")) {
                pf.setAccessible(true);
                try {
                    ((EditText) pf.get(mPickerDistance)).setTextColor(getResources().getColor(R.color.white));
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

package com.uw.hcde.fizzlab.trace.userInterface.drawing;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.userInterface.BaseActivity;

import java.util.ArrayList;

/**
 * Fragment that handles drawing.
 *
 * @author tianchi
 */
public class DrawFragment extends Fragment {

    private static final String TAG = "DrawFragment";
    public static final String KEY_RAW_POINTS = "raw_points";

    // Main buttons
    private View mButtonClear;
    private View mButtonNext;
    private DrawingView mDrawingView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_draw, container, false);
        ((BaseActivity) getActivity()).setNavigationTitle(R.string.draw);

        // Get views
        mButtonClear = view.findViewById(R.id.button_clear);
        mButtonNext = view.findViewById(R.id.button_next);
        mDrawingView = (DrawingView) view.findViewById(R.id.drawing_view_annotation);

        setupListener();
        return view;
    }

    /**
     * Helper function to add button listeners
     */
    private void setupListener() {
        mButtonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button clear clicked");
                mDrawingView.clear();
            }
        });

        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button next clicked");

                // Check if drawing is valid
                if (!mDrawingView.isValid()) {
                    mDrawingView.clear();
                    return;
                }

                ArrayList<Point> points = mDrawingView.getPoints();

                // Set fragment argument
                Fragment fragment = new AnnotationFragment();
                final Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(KEY_RAW_POINTS, points);
                fragment.setArguments(bundle);

                // Fragment transaction
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.slide_in, R.anim.slide_out, R.anim.slide_in_backstack, R.anim.slide_out_backstack)
                        .add(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}

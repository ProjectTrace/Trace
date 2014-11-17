package com.uw.hcde.fizzlab.trace.controller.walk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.parse.ParseUser;
import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.model.parse.ParseConstant;
import com.uw.hcde.fizzlab.trace.model.parse.ParseDataFactory;
import com.uw.hcde.fizzlab.trace.model.parse.ParseDrawing;
import com.uw.hcde.fizzlab.trace.model.parse.callback.ParseRetrieveCallback;

import java.util.List;

/**
 * Choose drawing
 *
 * @author tianchi
 */
public class ChooseDrawingActivity extends Activity implements ParseRetrieveCallback{

    private static final String TAG = "ChooseDrawingActivity";
    private static final String EXTRA_INT_DRAWING_IDENTIFIER = "drawing_identifier";

    private int mDrawingIndex;
    private List<ParseDrawing> mDrawings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_drawing);

        // Set navigation title
        TextView title = (TextView) findViewById(R.id.navigation_title);
        title.setText(getString(R.string.choose_drawing));

        mDrawingIndex = 0;

        // Next button
        View buttonNext = findViewById(R.id.button_next);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button next clicked");
                Intent intent = new Intent(ChooseDrawingActivity.this, ChooseDurationActivity.class);
                startActivity(intent);
            }
        });

        ParseDataFactory.retrieveDrawings(ParseUser.getCurrentUser(), this);
    }

    @Override
    public void retrieveCallback(int returnCode, List<ParseDrawing> drawings) {
        if (returnCode == ParseConstant.SUCCESS) {

        } else {

        }
    }
}

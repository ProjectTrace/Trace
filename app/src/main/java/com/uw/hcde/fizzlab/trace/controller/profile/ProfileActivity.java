package com.uw.hcde.fizzlab.trace.controller.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.parse.ParseUser;
import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.controller.main.DispatchActivity;
import com.uw.hcde.fizzlab.trace.controller.main.MainActivity;

/**
 * Profile activity that displays user information
 */
public class ProfileActivity extends Activity {

    private static final String TAG = "ProfileActivity";
    private View mButtonHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Sets navigation title
        TextView title = (TextView) findViewById(R.id.navigation_title);
        title.setText(getString(R.string.you));

        // Sets displayed username
        TextView userName = (TextView) findViewById(R.id.text_username);
        userName.setText(ParseUser.getCurrentUser().getUsername());

        // Back button
        mButtonHome = findViewById(R.id.navigation_button);
        mButtonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        // Sets up the log out button click handler
        View buttonLogout = findViewById(R.id.button_logout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Calls the Parse log out method
                ParseUser.logOut();

                // Clears root activity and switch to dispatch activity
                Intent intent = new Intent(ProfileActivity.this, DispatchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
}

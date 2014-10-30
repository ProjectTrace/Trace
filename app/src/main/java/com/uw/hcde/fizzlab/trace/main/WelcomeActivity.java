package com.uw.hcde.fizzlab.trace.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.uw.hcde.fizzlab.trace.R;

/**
 * Welcome screen that prompts username and password
 *
 * @author tianchi
 */
public class WelcomeActivity extends Activity {

    private static final String TAG = "WelcomeActivity";

    // UI references.
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private View mButtonSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Set up the login form
        mUsernameEditText = (EditText) findViewById(R.id.text_username);
        mPasswordEditText = (EditText) findViewById(R.id.text_password);

        // Set up the submit button click handler
        View buttonLogin = findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.d(TAG, "login clicked");
                login();
            }
        });

        mButtonSignUp = findViewById(R.id.button_sign_up);
        mButtonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "sign up clicked");
                Intent intent = new Intent(WelcomeActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Handles login logic
     */
    private void login() {
        String username = mUsernameEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString().trim();

        // Validate the log in data
        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.error_intro));
        if (username.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_username));
        }
        if (password.length() == 0) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_password));
        }
        validationErrorMessage.append(getString(R.string.error_end));

        // If there is a validation error, display the error
        if (validationError) {
            Toast toast = Toast.makeText(WelcomeActivity.this, validationErrorMessage.toString(), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }

        // Set up a progress dialog
        final ProgressDialog dialog = new ProgressDialog(WelcomeActivity.this);
        dialog.setMessage(getString(R.string.progress_login));
        dialog.show();

        // Call the Parse login method
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                dialog.dismiss();
                if (e != null) {
                    // Show the error message
                    Toast toast = Toast.makeText(WelcomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    // Start an intent for the dispatch activity
                    Intent intent = new Intent(WelcomeActivity.this, DispatchActivity.class);

                    // Clear root activity
                    // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }
}

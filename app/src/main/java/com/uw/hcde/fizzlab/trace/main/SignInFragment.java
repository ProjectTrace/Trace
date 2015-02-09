package com.uw.hcde.fizzlab.trace.main;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.utility.TraceUtil;

/**
 * Sign in screen that prompts username and password.
 *
 * @author tianchi
 */
public class SignInFragment extends Fragment {

    private static final String TAG = "SignInFragment";

    // UI references.
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private View mButtonSignUp;
    private View mButtonLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        // Sets up the login form
        mEmailEditText = (EditText) view.findViewById(R.id.text_email);
        mPasswordEditText = (EditText) view.findViewById(R.id.text_password);

        // Sets up the submit button click handler
        mButtonLogin = view.findViewById(R.id.button_login);
        mButtonSignUp = view.findViewById(R.id.button_sign_up);
        setupListener();

        return view;
    }


    /**
     * Helper function to add button listeners.
     */
    private void setupListener() {
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.d(TAG, "login clicked");
                login();
            }
        });

        mButtonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "sign up clicked");

                Fragment fragment = new SignUpFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.slide_in, R.anim.slide_out, R.anim.slide_in_backstack, R.anim.slide_out_backstack)
                        .add(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    /**
     * Handles login logic
     */
    private void login() {
        String email = mEmailEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString().trim();

        // Validate the log in data
        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.error_intro));
        if (email.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_email));
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
            TraceUtil.showToast(getActivity(), validationErrorMessage.toString());
            return;
        }

        // Set up a progress dialog
        final ProgressDialog dialog = new ProgressDialog(getActivity(), ProgressDialog.THEME_HOLO_DARK);
        dialog.setCancelable(false);
        dialog.setMessage(getString(R.string.progress_login));
        dialog.show();


        // Call the Parse login method
        ParseUser.logInInBackground(email, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                dialog.dismiss();
                if (e != null) {
                    // Show the error message
                    TraceUtil.showToast(getActivity(), e.getMessage());
                } else {
                    // Start an intent for the dispatch activity
                    Intent intent = new Intent(getActivity(), DispatchActivity.class);

                    // Clear root activity
                    // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }
}

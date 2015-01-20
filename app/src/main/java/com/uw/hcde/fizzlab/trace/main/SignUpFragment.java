package com.uw.hcde.fizzlab.trace.main;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.utility.TraceUtil;

/**
 * Sign up screen.
 *
 * @author tianchi
 */
public class SignUpFragment extends Fragment {

    private static final String TAG = "SignUpFragment";

    // UI references.
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText passwordAgainEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        // Get buttons
        usernameEditText = (EditText) view.findViewById(R.id.text_username);
        passwordEditText = (EditText) view.findViewById(R.id.text_password);
        passwordAgainEditText = (EditText) view.findViewById(R.id.text_password_again);

        // Set navigation bar
        TextView title = (TextView) view.findViewById(R.id.navigation_title);
        title.setText(getString(R.string.sign_up));

        TextView buttonBack = (TextView) view.findViewById(R.id.navigation_button);
        buttonBack.setText(getString(R.string.back));
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do I need to check stack count here?
                FragmentManager fm = getFragmentManager();
                fm.popBackStack();
            }
        });

        View buttonSignUp = view.findViewById(R.id.button_sign_up);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                signUp();
            }
        });

        return view;
    }

    /**
     * Signs up this user account if all input is valid.
     */
    private void signUp() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String passwordAgain = passwordAgainEditText.getText().toString().trim();

        // Validate the sign up data
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
        if (!password.equals(passwordAgain)) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_mismatched_passwords));
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
        dialog.setMessage(getString(R.string.progress_sign_up));
        dialog.show();

        // Set up a new Parse user
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);

        // Call Parse sign up method
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
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

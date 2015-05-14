package com.uw.hcde.fizzlab.trace.ui.welcome;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;
import com.parse.SignUpCallback;
import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.database.ParseConstant;
import com.uw.hcde.fizzlab.trace.database.ParseDataFactory;
import com.uw.hcde.fizzlab.trace.main.DispatchActivity;
import com.uw.hcde.fizzlab.trace.ui.BaseActivity;
import com.uw.hcde.fizzlab.trace.utility.TraceUtil;

/**
 * Sign up screen.
 *
 * @author tianchi
 */
public class SignUpFragment extends Fragment {

    private static final String TAG = "SignUpFragment";

    // UI references.
    private EditText emailEditText;
    private EditText nameEditText;
    private EditText passwordEditText;
    private EditText passwordAgainEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        // Get buttons
        emailEditText = (EditText) view.findViewById(R.id.text_email);
        nameEditText = (EditText) view.findViewById(R.id.text_name);
        passwordEditText = (EditText) view.findViewById(R.id.text_password);
        passwordAgainEditText = (EditText) view.findViewById(R.id.text_password_again);

        // Set navigation bar
        ((BaseActivity) getActivity()).enableNavigationBar();
        ((BaseActivity) getActivity()).enableBackButton();
        ((BaseActivity) getActivity()).setNavigationTitle(R.string.sign_up);

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
        final String email = emailEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String passwordAgain = passwordAgainEditText.getText().toString().trim();

        // Validate the sign up data
        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.error_intro));
        if (email.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_email));
        }

        if (name.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_name));
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
        final ParseUser user = new ParseUser();
        user.setUsername(email);
        user.setPassword(password);
        user.setEmail(email);
        user.put(ParseConstant.KEY_FULL_NAME, name);
        // Call Parse sign up method
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                dialog.dismiss();
                if (e != null) {
                    // Show the error message
                    TraceUtil.showToast(getActivity(), e.getMessage());
                } else {
                    // add username to installation table
                    ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                    installation.put("username", email);
                    installation.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d(TAG, "Success add email to installation table");
                                Log.d(TAG, "Send notification");
                                // test notification by say hello from trace team

                                // Create our Installation query
                                ParseQuery pushQuery = ParseInstallation.getQuery();
                                pushQuery.whereEqualTo("username", email); // Set the target email

                                // Send push notification to query
                                ParsePush push = new ParsePush();
                                push.setQuery(pushQuery);
                                push.setMessage("Welcome to Trace!");
                                push.sendInBackground(new SendCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Log.d(TAG, "Success send welcome notification");
                                        } else {
                                            Log.e(TAG, "Failed send welcome notification");
                                        }
                                    }
                                });
                            } else {
                                Log.e(TAG, "Failed add email to installation table" + e.getMessage());
                            }
                        }
                    });

                    // Add default friends and drawing
                    ParseDataFactory.addFriend(user, ParseConstant.TRACE_TEAM_EMAIL, null);
                    ParseDataFactory.addDefaultDrawing(user);

                    // Add myself
                    ParseDataFactory.addMySelfAsFriend(user);

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

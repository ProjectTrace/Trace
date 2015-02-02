package com.uw.hcde.fizzlab.trace.userInterface.profile;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseUser;
import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.main.DispatchActivity;
import com.uw.hcde.fizzlab.trace.main.MainActivity;

/**
 * Profile fragment.
 *
 * @author tianchi
 */
public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private View mButtonHome;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Sets navigation title
        TextView title = (TextView) view.findViewById(R.id.navigation_title);
        title.setText(getString(R.string.profile));

        // Sets displayed username
        TextView userName = (TextView) view.findViewById(R.id.text_username);
        userName.setText(ParseUser.getCurrentUser().getUsername());

        // Back button
        mButtonHome = view.findViewById(R.id.navigation_button_home);
        mButtonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        // Sets up the log out button click handler
        View buttonLogout = view.findViewById(R.id.button_logout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Calls the Parse log out method
                ParseUser.logOut();

                // Clears root activity and switch to dispatch activity
                Intent intent = new Intent(getActivity(), DispatchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        return view;
    }
}

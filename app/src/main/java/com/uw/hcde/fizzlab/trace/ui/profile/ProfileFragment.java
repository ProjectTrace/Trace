package com.uw.hcde.fizzlab.trace.ui.profile;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v13.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uw.hcde.fizzlab.trace.R;

/**
 * Profile fragment.
 *
 * @author tianchi
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private static final String TAB_FRIENDS = "tab_friends";
    private static final String TAB_DRAWN_PATH = "tab_drawn_path";

    private FragmentTabHost mTabHost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

//        // Sets navigation title
//        ((BaseActivity) getActivity()).setNavigationTitle(R.string.profile);
//
//        // Sets displayed username
//        TextView userName = (TextView) view.findViewById(R.id.text_username);
//        userName.setText(ParseUser.getCurrentUser().getUsername());
//
//        // Sets up the log out button click handler
//        View buttonLogout = view.findViewById(R.id.button_logout);
//        buttonLogout.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                // Calls the Parse log out method
//                ParseUser.logOut();
//
//                // Clears root activity and switch to dispatch activity
//                Intent intent = new Intent(getActivity(), DispatchActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//            }
//        });

        mTabHost = (FragmentTabHost) view.findViewById(R.id.tab_host);
        mTabHost.setup(getActivity(), getActivity().getFragmentManager(), R.id.tab_content);

        // Create friends tab
        mTabHost.addTab(mTabHost.newTabSpec(TAB_FRIENDS).setIndicator(getString(R.string.friends)),
                FriendsFragment.class, null);

        // Create my drawn path tab
        mTabHost.addTab(mTabHost.newTabSpec(TAB_DRAWN_PATH).setIndicator(getString(R.string.my_drawn_path)),
                DrawnPathsFragment.class, null);

        return view;
    }
}

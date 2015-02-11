package com.uw.hcde.fizzlab.trace.ui.profile;


import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v13.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
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


        // Locate android.R.id.tabhost in main_fragment.xml
        mTabHost = (FragmentTabHost) view.findViewById(R.id.tab_host);

        // Create the tabs in main_fragment.xml
        mTabHost.setup(getActivity(), getActivity().getFragmentManager(), R.id.tab_content);

        // Create Tab1 with a custom image in res folder
        mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator("Tab1"),
                FirstFragment.class, null);

        // Create Tab2
        mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator("Tab2"),
                SecondFragment.class, null);

        return view;
    }

//    private class MyPagerAdapter extends FragmentPagerAdapter {
//        private int NUM_ITEMS = 3;
//
//        public MyPagerAdapter(FragmentManager fragmentManager) {
//            super(fragmentManager);
//        }
//
//        // Returns total number of pages
//        @Override
//        public int getCount() {
//            return NUM_ITEMS;
//        }
//
//        // Returns the fragment to display for that page
//        @Override
//        public Fragment getItem(int position) {
//            switch (position) {
//                case 0:
//                    return new FirstFragment();
//                case 1:
//                    return new SecondFragment();
//                case 2:
//                    return new FirstFragment();
//                default:
//                    return null;
//            }
//        }
//
//        // Returns the page title for the top indicator
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return "Page " + position;
//        }
//    }
}

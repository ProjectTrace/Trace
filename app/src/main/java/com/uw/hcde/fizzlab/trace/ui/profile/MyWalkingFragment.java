package com.uw.hcde.fizzlab.trace.ui.profile;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.parse.ParseUser;
import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.database.ParseDataFactory;
import com.uw.hcde.fizzlab.trace.database.ParseDrawing;

import java.util.List;

/**
 * Created by yellowleaf on 5/1/15.
 */
public class MyWalkingFragment extends Fragment {
    private static final String TAG = "WalkedPathsFragment";
    private ListView mDrawnPathList;
    private List<ParseDrawing> mDrawings;
    private MyWalkedPathAdapter mAdapter;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drawn_paths, container, false);
        mDrawnPathList = (ListView) view.findViewById(R.id.list);
        mContext = getActivity();
        mAdapter = null;

        mDrawings = ParseDataFactory.retrieveMyWalkedPaths(ParseUser.getCurrentUser());

        return view;
    }

    private class MyWalkedPathAdapter {
    }
}

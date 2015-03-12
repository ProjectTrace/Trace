package com.uw.hcde.fizzlab.trace.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uw.hcde.fizzlab.trace.R;
import com.uw.hcde.fizzlab.trace.utility.TraceAppPreference;

/**
 * Term of Service fragment
 *
 * @author tianchi
 */
public class TermOfServiceFragment extends Fragment {

    private View mButtonAccept;
    private View mButtonDecline;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_term_of_service, container, false);

        // Set navigation title
        ((BaseActivity) getActivity()).setNavigationTitle(R.string.term_of_service);
        mButtonAccept = view.findViewById(R.id.button_accept);
        mButtonDecline = view.findViewById(R.id.button_decline);
        setupListeners();

        TextView text = (TextView) view.findViewById(R.id.content);
        text.setMovementMethod(new ScrollingMovementMethod());

        return view;
    }

    private void setupListeners() {
        mButtonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TraceAppPreference.setTermAccepted(getActivity());
                getActivity().finish();
            }
        });

        mButtonDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }
}

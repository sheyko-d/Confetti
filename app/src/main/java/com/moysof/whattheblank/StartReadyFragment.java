package com.moysof.whattheblank;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StartReadyFragment extends Fragment {

    private static int sNumberCards;

    public static StartReadyFragment newInstance(int numberCards) {
        sNumberCards = numberCards;

        return new StartReadyFragment();
    }

    public StartReadyFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_start_ready, container, false);

        ((TextView) rootView.findViewById(R.id.start_ready_desc_txt)).setText
                (String.format(getResources().getString(R.string.start_ready_desc), sNumberCards));


        StartGameActivity.sViewPager.setSwipeEnabled(false);

        rootView.findViewById(R.id.start_ready_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartGameActivity.sViewPager.setCurrentItem(1);
            }
        });

        return rootView;
    }

}
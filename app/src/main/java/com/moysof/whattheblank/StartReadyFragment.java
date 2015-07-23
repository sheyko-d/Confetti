package com.moysof.whattheblank;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class StartReadyFragment extends Fragment {

    public static StartReadyFragment newInstance() {
        StartReadyFragment fragment = new StartReadyFragment();
        return fragment;
    }

    public StartReadyFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_start_ready, container, false);


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
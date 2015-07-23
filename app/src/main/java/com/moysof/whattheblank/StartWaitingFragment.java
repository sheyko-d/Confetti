package com.moysof.whattheblank;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.hdodenhof.circleimageview.CircleImageView;

public class StartWaitingFragment extends Fragment {

    private ViewGroup mOrderLayout;

    public static StartWaitingFragment newInstance() {
        StartWaitingFragment fragment = new StartWaitingFragment();
        return fragment;
    }

    public StartWaitingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_start_waiting, container, false);

        mOrderLayout = (ViewGroup) rootView.findViewById(R.id.start_waiting_order_layout);

        addTeamCircle(R.color.yellow);
        addTeamDivider();
        addTeamCircle(R.color.green);
        addTeamDivider();
        addTeamCircle(R.color.blue);

        StartGameActivity.sViewPager.setSwipeEnabled(false);

        rootView.findViewById(R.id.start_waiting_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartGameActivity.sViewPager.setCurrentItem(5);
            }
        });

        return rootView;
    }

    private void addTeamCircle(int color) {
        CircleImageView teamImg = (CircleImageView) LayoutInflater.from(getActivity())
                .inflate(R.layout.img_start_team, null);
        teamImg.setImageResource(color);
        teamImg.setLayoutParams(new ViewGroup.LayoutParams(Util.convertDpToPixel(36),
                Util.convertDpToPixel(36)));
        mOrderLayout.addView(teamImg);
    }

    private void addTeamDivider() {
        View teamDivider = LayoutInflater.from(getActivity())
                .inflate(R.layout.divider_start_team, null);
        teamDivider.setLayoutParams(new ViewGroup.LayoutParams(Util.convertDpToPixel(36),
                Util.convertDpToPixel(36)));
        mOrderLayout.addView(teamDivider);
    }

}
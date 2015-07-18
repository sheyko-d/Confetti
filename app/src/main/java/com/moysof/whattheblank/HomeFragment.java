package com.moysof.whattheblank;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class HomeFragment extends Fragment {

    private Button mJoinBtn;
    private Button mHostBtn;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        mJoinBtn = (Button) rootView.findViewById(R.id.home_join_btn);
        mHostBtn = (Button) rootView.findViewById(R.id.home_host_btn);

        mJoinBtn.setOnClickListener(mGameClickListener);
        mHostBtn.setOnClickListener(mGameClickListener);

        return rootView;
    }

    View.OnClickListener mGameClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.home_join_btn) {
                joinGame();
            } else {
                hostGame();
            }
        }
    };

    public void joinGame() {
        startActivity(new Intent(getActivity(), JoinActivity.class));
    }

    public void hostGame() {
        startActivity(new Intent(getActivity(), HostActivity.class));
    }
}
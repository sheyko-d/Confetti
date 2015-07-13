package com.moysof.whattheblank;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class JoinIDFragment extends Fragment {

    public static JoinIDFragment newInstance() {
        JoinIDFragment fragment = new JoinIDFragment();
        return fragment;
    }

    public JoinIDFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_join_id, container, false);

        return rootView;
    }

}
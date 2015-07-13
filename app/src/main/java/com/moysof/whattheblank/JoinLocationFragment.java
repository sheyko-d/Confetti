package com.moysof.whattheblank;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class JoinLocationFragment extends Fragment {

    public static JoinLocationFragment newInstance() {
        JoinLocationFragment fragment = new JoinLocationFragment();
        return fragment;
    }

    public JoinLocationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_join_location, container, false);

        return rootView;
    }

}
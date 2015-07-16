package com.moysof.whattheblank;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LobbyGameFragment extends Fragment {

    public static LobbyGameFragment newInstance() {
        LobbyGameFragment fragment = new LobbyGameFragment();
        return fragment;
    }

    public LobbyGameFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lobby_game, container, false);

        return rootView;
    }

}
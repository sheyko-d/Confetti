package com.moysof.whattheblank;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class JoinLobbyGameFragment extends Fragment {

    public static JoinLobbyGameFragment newInstance() {
        JoinLobbyGameFragment fragment = new JoinLobbyGameFragment();
        return fragment;
    }

    public JoinLobbyGameFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_join_lobby_game, container, false);

        return rootView;
    }

}
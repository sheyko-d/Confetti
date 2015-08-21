package com.moysof.confetti;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class JoinLobbyGameFragment extends Fragment {

    private static int sNumberTeams;
    private static int sNumberPlayers;
    private static int sNumberCards;
    private static int sTime;
    private static int sAssignedNumber;
    private int mTotalPlayersCount;
    private TextView mPlayersTxt;

    public static JoinLobbyGameFragment newInstance(int numberTeams, int numberPlayers,
                                                    int numberCards, int time, int assignedNumber) {
        sNumberTeams = numberTeams;
        sNumberPlayers = numberPlayers;
        sNumberCards = numberCards;
        sTime = time;
        sAssignedNumber = assignedNumber;

        return new JoinLobbyGameFragment();
    }

    public JoinLobbyGameFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_join_lobby_game, container, false);

        mPlayersTxt = (TextView) rootView.findViewById(R.id.join_lobby_players_txt);

        mTotalPlayersCount = sNumberTeams * sNumberPlayers;

        mPlayersTxt.setText(sAssignedNumber
                + " of " + mTotalPlayersCount);
        ((TextView) rootView.findViewById(R.id.join_lobby_cards_txt)).setText(sNumberCards
                + " cards per player");
        ((TextView) rootView.findViewById(R.id.join_lobby_time_txt)).setText(sTime + " seconds");

        return rootView;
    }

    public void updateAssignedNumber(int assignedNumber) {
        if (assignedNumber != -1) {
            sAssignedNumber = assignedNumber;
            mPlayersTxt.setText(sAssignedNumber + " of " + mTotalPlayersCount);
        }
    }

}
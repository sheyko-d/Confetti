package com.moysof.whattheblank;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moysof.whattheblank.adapter.PlayersAdapter;

public class JoinLobbyPlayersFragment extends Fragment {
    private PlayersAdapter mAdapter;
    private SortedList mPlayers = new SortedList<>(PlayersAdapter.Player.class,
            new SortedList.Callback<PlayersAdapter.Player>() {
                @Override
                public int compare(PlayersAdapter.Player o1, PlayersAdapter.Player o2) {
                    int i = o1.getTeamColor().compareTo(o2.getTeamColor());
                    if (i != 0) return i;

                    return o1.getType().compareTo(o2.getType());
                }

                @Override
                public void onInserted(int position, int count) {
                    mAdapter.notifyItemRangeInserted(position, count);
                }

                @Override
                public void onRemoved(int position, int count) {
                    mAdapter.notifyItemRangeRemoved(position, count);
                }

                @Override
                public void onMoved(int fromPosition, int toPosition) {
                    mAdapter.notifyItemMoved(fromPosition, toPosition);
                }

                @Override
                public void onChanged(int position, int count) {
                    mAdapter.notifyItemRangeChanged(position, count);
                }

                @Override
                public boolean areContentsTheSame(PlayersAdapter.Player oldItem, PlayersAdapter.Player newItem) {
                    // return whether the items' visual representations are the same or not.
                    return oldItem.getType().equals(newItem.getType()) && oldItem.getName()
                            .equals(newItem.getName()) && oldItem.getUsername()
                            .equalsIgnoreCase(oldItem.getUsername());
                }

                @Override
                public boolean areItemsTheSame(PlayersAdapter.Player item1, PlayersAdapter.Player item2) {
                    return item1.getType().equals(item2.getType()) && item1.getPlayerId()
                            .equals(item2.getPlayerId());
                }
            });

    public static JoinLobbyPlayersFragment newInstance() {
        JoinLobbyPlayersFragment fragment = new JoinLobbyPlayersFragment();
        return fragment;
    }

    public JoinLobbyPlayersFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_join_lobby_players, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView
                .findViewById(R.id.lobby_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new PlayersAdapter(getActivity(), mPlayers);
        recyclerView.setAdapter(mAdapter);

        mPlayers.clear();
        mPlayers.beginBatchedUpdates();
        mPlayers.add(new PlayersAdapter.Player(PlayersAdapter.ITEM_TYPE_PLAYER, "0", "Dan Trevor",
                "dtizzle", "https://www.gravatar.com/avatar/ba0ebce7e385e4c242c9401ff9d551b2?s=328" +
                "&d=identicon&r=PG&f=1", getResources().getColor(R.color.green)));
        mPlayers.add(new PlayersAdapter.Player(PlayersAdapter.ITEM_TYPE_PLAYER, "1", "Jen Sadie",
                "dtizzle", "https://www.gravatar.com/avatar/ba0ebce7e385e4c242c9401ff9d551b2?s=328" +
                "&d=identicon&r=PG&f=1", getResources().getColor(R.color.green)));
        mPlayers.add(new PlayersAdapter.Player(PlayersAdapter.ITEM_TYPE_PLAYER, "2", "Adam Charter",
                "dtizzle", "https://www.gravatar.com/avatar/ba0ebce7e385e4c242c9401ff9d551b2?s=328" +
                "&d=identicon&r=PG&f=1", getResources().getColor(R.color.blue)));
        mPlayers.add(new PlayersAdapter.Player(PlayersAdapter.ITEM_TYPE_PLAYER, "2", "Adam Charter",
                "dtizzle", "https://www.gravatar.com/avatar/ba0ebce7e385e4c242c9401ff9d551b2?s=328" +
                "&d=identicon&r=PG&f=1", getResources().getColor(R.color.yellow)));
        mPlayers.add(new PlayersAdapter.Player(PlayersAdapter.ITEM_TYPE_PLAYER, "2", "Adam Sadie",
                "dtizzle", "https://www.gravatar.com/avatar/ba0ebce7e385e4c242c9401ff9d551b2?s=328" +
                "&d=identicon&r=PG&f=1", getResources().getColor(R.color.yellow)));
        mPlayers.add(new PlayersAdapter.Player(PlayersAdapter.ITEM_TYPE_SPACE,
                getResources().getColor(R.color.green)));
        mPlayers.add(new PlayersAdapter.Player(PlayersAdapter.ITEM_TYPE_SPACE,
                getResources().getColor(R.color.blue)));
        mPlayers.add(new PlayersAdapter.Player(PlayersAdapter.ITEM_TYPE_SPACE,
                getResources().getColor(R.color.yellow)));
        mPlayers.endBatchedUpdates();

        return rootView;
    }

}
package com.moysof.whattheblank;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moysof.whattheblank.adapter.HostPlayersAdapter;

public class HostLobbyPlayersFragment extends Fragment {

    private HostPlayersAdapter mAdapter;
    private SortedList mPlayers = new SortedList<>(HostPlayersAdapter.Player.class,
            new SortedList.Callback<HostPlayersAdapter.Player>() {
                @Override
                public int compare(HostPlayersAdapter.Player o1, HostPlayersAdapter.Player o2) {
                    return o1.getTeamColor().compareTo(o2.getTeamColor());
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
                public boolean areContentsTheSame(HostPlayersAdapter.Player oldItem,
                                                  HostPlayersAdapter.Player newItem) {
                    // return whether the items' visual representations are the same or not.
                    return oldItem.getName().equals(newItem.getName()) && oldItem
                            .getUsername().equals(newItem.getUsername())
                            && oldItem.getTeamColor().equals(oldItem.getTeamColor());
                }

                @Override
                public boolean areItemsTheSame(HostPlayersAdapter.Player item1,
                                               HostPlayersAdapter.Player item2) {
                    return item1.getPlayerId().equals(item2.getPlayerId());
                }
            });

    public static HostLobbyPlayersFragment newInstance() {
        HostLobbyPlayersFragment fragment = new HostLobbyPlayersFragment();
        return fragment;
    }

    public HostLobbyPlayersFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_host_lobby_players, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView
                .findViewById(R.id.host_lobby_players_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new HostPlayersAdapter(getActivity(), mPlayers);
        recyclerView.setAdapter(mAdapter);

        mPlayers.clear();
        mPlayers.beginBatchedUpdates();
        mPlayers.add(new HostPlayersAdapter.Player("0", "Dan Trevor", "dtizzle",
                getResources().getColor(R.color.green), "https://www.gravatar.com/avatar/ba0ebce7e385e4c242c9401ff9d551b2?s=328&d=identicon&r=PG&f=1"));
        mPlayers.add(new HostPlayersAdapter.Player("1", "Jen Sadie", "flyingsazzy",
                getResources().getColor(R.color.green), "https://www.gravatar.com/avatar/ba0ebce7e385e4c242c9401ff9d551b2?s=328&d=identicon&r=PG&f=1"));
        mPlayers.add(new HostPlayersAdapter.Player("2", "Adam Charter", "ac123",
                getResources().getColor(R.color.yellow), "https://www.gravatar.com/avatar/ba0ebce7e385e4c242c9401ff9d551b2?s=328&d=identicon&r=PG&f=1"));
        mPlayers.add(new HostPlayersAdapter.Player("3", "Bev Bridge", "acdk83",
                getResources().getColor(R.color.blue), "https://www.gravatar.com/avatar/ba0ebce7e385e4c242c9401ff9d551b2?s=328&d=identicon&r=PG&f=1"));
        mPlayers.add(new HostPlayersAdapter.Player("4", "Shelly Chu", "chuchu",
                getResources().getColor(R.color.blue), "https://www.gravatar.com/avatar/ba0ebce7e385e4c242c9401ff9d551b2?s=328&d=identicon&r=PG&f=1"));
        mPlayers.add(new HostPlayersAdapter.Player("5", "Tommy Greene", "flyingsazzy",
                getResources().getColor(R.color.blue), "https://www.gravatar.com/avatar/ba0ebce7e385e4c242c9401ff9d551b2?s=328&d=identicon&r=PG&f=1"));
        mPlayers.endBatchedUpdates();

        return rootView;
    }

}
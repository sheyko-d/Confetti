package com.moysof.whattheblank;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moysof.whattheblank.adapter.HostTeamsAdapter;

public class HostLobbyGameFragment extends Fragment {

    private HostTeamsAdapter mAdapter;
    private SortedList mTeams = new SortedList<>(HostTeamsAdapter.Team.class,
            new SortedList.Callback<HostTeamsAdapter.Team>() {
                @Override
                public int compare(HostTeamsAdapter.Team o1, HostTeamsAdapter.Team o2) {
                    return o1.getNumber().compareTo(o2.getNumber());
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
                public boolean areContentsTheSame(HostTeamsAdapter.Team oldItem,
                                                  HostTeamsAdapter.Team newItem) {
                    // return whether the items' visual representations are the same or not.
                    return oldItem.getNumber().equals(newItem.getNumber()) && oldItem
                            .getAssignedCount().equals(newItem.getAssignedCount())
                            && oldItem.getColor().equals(oldItem.getColor());
                }

                @Override
                public boolean areItemsTheSame(HostTeamsAdapter.Team item1,
                                               HostTeamsAdapter.Team item2) {
                    return item1.getNumber().equals(item2.getNumber());
                }
            });

    public static HostLobbyGameFragment newInstance() {
        HostLobbyGameFragment fragment = new HostLobbyGameFragment();
        return fragment;
    }

    public HostLobbyGameFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_host_lobby_game, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView
                .findViewById(R.id.host_lobby_game_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new HostTeamsAdapter(getActivity(), mTeams);
        recyclerView.setAdapter(mAdapter);

        mTeams.clear();
        mTeams.beginBatchedUpdates();
        mTeams.add(new HostTeamsAdapter.Team(1, 2, getResources().getColor(R.color.green)));
        mTeams.add(new HostTeamsAdapter.Team(2, 1, getResources().getColor(R.color.yellow)));
        mTeams.add(new HostTeamsAdapter.Team(3, 3, getResources().getColor(R.color.blue)));
        mTeams.endBatchedUpdates();

        return rootView;
    }

}
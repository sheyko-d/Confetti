package com.moysof.whattheblank;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moysof.whattheblank.adapter.JoinAdapter;

public class JoinLocationFragment extends Fragment {
    private JoinAdapter mAdapter;
    private SortedList mGames = new SortedList<>(JoinAdapter.Game.class,
            new SortedList.Callback<JoinAdapter.Game>() {
                @Override
                public int compare(JoinAdapter.Game o1, JoinAdapter.Game o2) {
                    return o1.getGameId().compareTo(o2.getGameId());
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
                public boolean areContentsTheSame(JoinAdapter.Game oldItem, JoinAdapter.Game newItem) {
                    // return whether the items' visual representations are the same or not.
                    return oldItem.getType().equals(newItem.getType()) && oldItem.getName()
                            .equals(newItem.getName()) && oldItem.getUsername()
                            .equalsIgnoreCase(oldItem.getUsername());
                }

                @Override
                public boolean areItemsTheSame(JoinAdapter.Game item1, JoinAdapter.Game item2) {
                    return item1.getGameId().equals(item2.getGameId());
                }
            });

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

        RecyclerView recyclerView = (RecyclerView) rootView
                .findViewById(R.id.join_location_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new JoinAdapter(getActivity(), mGames);
        recyclerView.setAdapter(mAdapter);

        mGames.clear();
        mGames.beginBatchedUpdates();
        mGames.add(new JoinAdapter.Game(JoinAdapter.ITEM_TYPE_GAME_FRIEND, "0", "Wicked Fun Game",
                "sd1234"));
        mGames.add(new JoinAdapter.Game(JoinAdapter.ITEM_TYPE_GAME, "1", "Play Us",
                "unko"));
        mGames.add(new JoinAdapter.Game(JoinAdapter.ITEM_TYPE_GAME, "2", "Game123",
                "thisguy99"));
        mGames.endBatchedUpdates();

        return rootView;
    }

}
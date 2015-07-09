package com.moysof.whattheblank;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moysof.whattheblank.adapter.FriendsAdapter;

public class FriendsFragment extends Fragment {
    private RecyclerView mRecycler;
    private LinearLayoutManager mLayoutManager;
    private FriendsAdapter mAdapter;
    private SwipeRefreshLayout mRefreshLayout;
    private SortedList mFriends = new SortedList<>(FriendsAdapter.Friend.class,
            new SortedList.Callback<FriendsAdapter.Friend>() {
                @Override
                public int compare(FriendsAdapter.Friend o1, FriendsAdapter.Friend o2) {
                    // Sort items, new ones first
                    int i = o1.getType().compareTo(o2.getType());
                    if (i != 0) return i;

                    return o1.getName().compareTo(o2.getName());
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
                public boolean areContentsTheSame(FriendsAdapter.Friend oldItem, FriendsAdapter.Friend newItem) {
                    // return whether the items' visual representations are the same or not.
                    return oldItem.getType().equals(newItem.getType()) && oldItem.getName()
                            .equalsIgnoreCase(oldItem.getName());
                }

                @Override
                public boolean areItemsTheSame(FriendsAdapter.Friend item1, FriendsAdapter.Friend item2) {
                    return item1.getUserId().equals(item2.getUserId());
                }
            });

    public static FriendsFragment newInstance() {
        FriendsFragment fragment = new FriendsFragment();
        return fragment;
    }

    public FriendsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        mRecycler = (RecyclerView) rootView.findViewById(R.id.friends_recycler_view);
        mRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.friends_refresh_layout);

        initRecycler();
        initRefreshLayout();

        return rootView;
    }

    private void initRecycler() {
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecycler.setLayoutManager(mLayoutManager);

        mAdapter = new FriendsAdapter(getActivity(), mFriends);
        mRecycler.setAdapter(mAdapter);

        mFriends.clear();
        mFriends.add(new FriendsAdapter.Friend(getString(R.string.friends_requests_header),
                FriendsAdapter.ITEM_TYPE_REQUESTS_HEADER));
        mFriends.add(new FriendsAdapter.Friend("0", "https://www.gravatar.com/avatar/ba0ebce7e385e4c242c9401ff9d551b2?s=328&d=identicon&r=PG&f=1", "Dan Trevor", "@dtizzle", FriendsAdapter.ITEM_TYPE_REQUESTS_FRIEND));
        mFriends.add(new FriendsAdapter.Friend("1", "https://www.gravatar.com/avatar/ee6e12042dc31b1ef27471482f9ff91f?s=328&d=identicon&r=PG&f=1", "Sammy Davis", "@flyingsazzy", FriendsAdapter.ITEM_TYPE_REQUESTS_FRIEND));
        mFriends.add(new FriendsAdapter.Friend(getString(R.string.friends_friends_header),
                FriendsAdapter.ITEM_TYPE_FRIENDS_HEADER));
        mFriends.add(new FriendsAdapter.Friend("0", "https://www.gravatar.com/avatar/ba0ebce7e385e4c242c9401ff9d551b2?s=328&d=identicon&r=PG&f=1", "Dan Trevor", "@dtizzle", FriendsAdapter.ITEM_TYPE_FRIENDS_FRIEND));
        mFriends.add(new FriendsAdapter.Friend("1", "https://www.gravatar.com/avatar/ee6e12042dc31b1ef27471482f9ff91f?s=328&d=identicon&r=PG&f=1", "Sammy Davis", "@flyingsazzy", FriendsAdapter.ITEM_TYPE_FRIENDS_FRIEND));
        mFriends.add(new FriendsAdapter.Friend(getString(R.string.friends_facebook_header),
                FriendsAdapter.ITEM_TYPE_FACEBOOK_HEADER));
        mFriends.add(new FriendsAdapter.Friend("0", "https://www.gravatar.com/avatar/ba0ebce7e385e4c242c9401ff9d551b2?s=328&d=identicon&r=PG&f=1", "Dan Trevor", "@dtizzle", FriendsAdapter.ITEM_TYPE_FACEBOOK_FRIEND));
        mFriends.add(new FriendsAdapter.Friend("1", "https://www.gravatar.com/avatar/ee6e12042dc31b1ef27471482f9ff91f?s=328&d=identicon&r=PG&f=1", "Sammy Davis", "@flyingsazzy", FriendsAdapter.ITEM_TYPE_FACEBOOK_FRIEND));
    }

    private void initRefreshLayout() {
        mRefreshLayout.setColorSchemeResources(R.color.primary, R.color.green, R.color.red,
                R.color.blue);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mRefreshLayout.setRefreshing(false);
                            }
                        });
                    }
                }, 2000);
            }
        });
    }

}
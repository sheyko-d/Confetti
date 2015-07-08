package com.moysof.whattheblank;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moysof.whattheblank.adapter.ChatAdapter;

import java.util.ArrayList;

public class ChatFragment extends Fragment {

    private RecyclerView mRecycler;
    private LinearLayoutManager mLayoutManager;
    private ChatAdapter mAdapter;
    private SwipeRefreshLayout mRefreshLayout;
    private ArrayList<ChatAdapter.ChatThread> mThreads = new ArrayList<>();

    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
        return fragment;
    }

    public ChatFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        mRecycler = (RecyclerView) rootView.findViewById(R.id.chat_recycler_view);
        mRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.chat_refresh_layout);

        initRecycler();
        initRefreshLayout();

        return rootView;
    }

    private void initRecycler() {
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecycler.setLayoutManager(mLayoutManager);

        mThreads.clear();
        mThreads.add(new ChatAdapter.ChatThread("0", "https://www.gravatar.com/avatar/ba0ebce7e385e4c242c9401ff9d551b2?s=328&d=identicon&r=PG&f=1", "Dan Trevor", "Sammy's not answering!",
                "Now"));
        mThreads.add(new ChatAdapter.ChatThread("1", "https://www.gravatar.com/avatar/ee6e12042dc31b1ef27471482f9ff91f?s=328&d=identicon&r=PG&f=1", "Sammy Davis", "You: Sammy are you around",
                "6 min"));

        mAdapter = new ChatAdapter(getActivity(), mThreads);
        mRecycler.setAdapter(mAdapter);
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
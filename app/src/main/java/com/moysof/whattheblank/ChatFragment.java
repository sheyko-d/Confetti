package com.moysof.whattheblank;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ChatFragment extends Fragment {

    private RecyclerView mRecycler;
    private LinearLayoutManager mLayoutManager;

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

        initRecycler();
        
        return rootView;
    }

    private void initRecycler() {
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecycler.setLayoutManager(mLayoutManager);
    }
}
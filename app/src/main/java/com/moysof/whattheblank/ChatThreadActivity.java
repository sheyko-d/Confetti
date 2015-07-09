package com.moysof.whattheblank;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.moysof.whattheblank.adapter.ChatThreadAdapter;

import java.util.ArrayList;


public class ChatThreadActivity extends AppCompatActivity {

    private RecyclerView mRecycler;
    private LinearLayoutManager mLayoutManager;
    private ChatThreadAdapter mAdapter;
    private ArrayList<ChatThreadAdapter.ChatMessage> mMessages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_thread);

        String title = getIntent().getStringExtra(Util.EXTRA_AUTHOR);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(title);
            actionBar.setSubtitle("Online"); //TODO: Replace with actual value
        }

        mRecycler = (RecyclerView) findViewById(R.id.thread_recycler_view);

        initRecycler();
    }

    private void initRecycler() {
        mLayoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(mLayoutManager);

        mMessages.clear();
        mMessages.add(new ChatThreadAdapter.ChatMessage("0", "https://www.gravatar.com/avatar/ba0ebce7e385e4c242c9401ff9d551b2?s=328&d=identicon&r=PG&f=1", "Sammy's not answering!",
                "Now", false));
        mMessages.add(new ChatThreadAdapter.ChatMessage("1", "Sammy are you around",
                "6 min", true));

        mAdapter = new ChatThreadAdapter(this, mMessages);
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

}
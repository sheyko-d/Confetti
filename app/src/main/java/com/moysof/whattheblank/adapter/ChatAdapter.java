package com.moysof.whattheblank.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moysof.whattheblank.ChatThreadActivity;
import com.moysof.whattheblank.R;
import com.moysof.whattheblank.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;

public class ChatAdapter extends
        RecyclerView.Adapter<ChatAdapter.ChatThreadHolder> {

    private Context mContext;
    private ImageLoader mImageLoader;
    private ArrayList<ChatThread> threads;

    public ChatAdapter(Context context, ArrayList<ChatThread> threads) {
        this.threads = threads;
        mContext = context;

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(250, true, false, false))
                .showImageOnLoading(android.R.color.white)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).defaultDisplayImageOptions(defaultOptions).build();
        ImageLoader.getInstance().init(config);
        mImageLoader = ImageLoader.getInstance();
    }

    public static class ChatThread {

        public String threadId;
        public String avatar;
        public String author;
        public String snippet;
        public String time;

        public ChatThread(String threadId, String avatar, String author, String snippet,
                          String time) {
            this.threadId = threadId;
            this.avatar = avatar;
            this.author = author;
            this.snippet = snippet;
            this.time = time;
        }

        public String getThreadId() {
            return threadId;
        }

        public String getAvatar() {
            return avatar;
        }

        public String getAuthor() {
            return author;
        }

        public String getSnippet() {
            return snippet;
        }

        public String getTime() {
            return time;
        }
    }

    public class ChatThreadHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView avatarImg;
        public TextView authorTxt;
        public TextView snippetTxt;
        public TextView timeTxt;

        public ChatThreadHolder(View v) {
            super(v);
            avatarImg = (ImageView) v.findViewById(R.id.chat_img);
            authorTxt = (TextView) v.findViewById(R.id.chat_author_txt);
            snippetTxt = (TextView) v.findViewById(R.id.chat_snippet_txt);
            timeTxt = (TextView) v.findViewById(R.id.chat_time_txt);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            chatClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ChatThreadHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        return new ChatThreadHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_chat, parent, false));
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ChatThreadHolder holder, int position) {
        ChatThread thread = threads.get(position);

        mImageLoader.displayImage(thread.getAvatar(), holder.avatarImg);
        holder.authorTxt.setText(thread.getAuthor());
        holder.snippetTxt.setText(thread.getSnippet());
        holder.timeTxt.setText(thread.getTime());
    }

    @Override
    public int getItemCount() {
        return threads.size();
    }

    OnItemClickListener chatClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(View v, int position) {
            ChatThread thread = threads.get(position);
            mContext.startActivity(new Intent(mContext, ChatThreadActivity.class)
                    .putExtra(Util.EXTRA_AUTHOR, thread.getAuthor()));
        }

    };
}
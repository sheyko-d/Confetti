package com.moysof.whattheblank.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moysof.whattheblank.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;

public class ChatThreadAdapter extends
        RecyclerView.Adapter<ChatThreadAdapter.ChatMessageHolder> {

    private static final int ITEM_TYPE_MESSAGE_IN = 0;
    private static final int ITEM_TYPE_MESSAGE_OUT = 1;
    private ImageLoader mImageLoader;
    private ArrayList<ChatMessage> messages;

    public ChatThreadAdapter(Context context, ArrayList<ChatMessage> messages) {
        this.messages = messages;

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

    public static class ChatMessage {

        public String messageId;
        public String avatar;
        public String message;
        public String time;
        public Boolean isMine;

        public ChatMessage(String messageId, String avatar, String message,
                           String time, Boolean isMine) {
            this.messageId = messageId;
            this.avatar = avatar;
            this.message = message;
            this.time = time;
            this.isMine = isMine;
        }

        public ChatMessage(String messageId, String message,
                           String time, Boolean isMine) {
            this.messageId = messageId;
            this.message = message;
            this.time = time;
            this.isMine = isMine;
        }

        public String getMessageId() {
            return messageId;
        }

        public String getAvatar() {
            return avatar;
        }

        public String getMessage() {
            return message;
        }

        public String getTime() {
            return time;
        }

        public Boolean isMine() {
            return isMine;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).isMine()) {
            return ITEM_TYPE_MESSAGE_OUT;
        } else {
            return ITEM_TYPE_MESSAGE_IN;
        }
    }

    public class ChatMessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView avatarImg;
        public TextView messageTxt;
        public TextView timeTxt;

        public ChatMessageHolder(View v) {
            super(v);
            avatarImg = (ImageView) v.findViewById(R.id.thread_img);
            messageTxt = (TextView) v.findViewById(R.id.thread_message_txt);
            timeTxt = (TextView) v.findViewById(R.id.thread_time_txt);

            //v.setOnClickListener(this);
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
    public ChatMessageHolder onCreateViewHolder(ViewGroup parent,
                                                int viewType) {
        if (viewType == ITEM_TYPE_MESSAGE_IN) {
            return new ChatMessageHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_message_in, parent, false));
        } else {
            return new ChatMessageHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_message_out, parent, false));
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ChatMessageHolder holder, int position) {
        ChatMessage message = messages.get(position);

        if (!message.isMine()) {
            mImageLoader.displayImage(message.getAvatar(), holder.avatarImg);
        }
        holder.messageTxt.setText(message.getMessage());
        holder.timeTxt.setText(message.getTime());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    OnItemClickListener chatClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(View v, int position) {
        }

    };
}
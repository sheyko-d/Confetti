package com.moysof.whattheblank.adapter;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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

public class FriendsAdapter extends
        RecyclerView.Adapter<FriendsAdapter.FriendHolder> {

    public static final int ITEM_TYPE_REQUESTS_HEADER = 0;
    public static final int ITEM_TYPE_REQUESTS_FRIEND = 1;
    public static final int ITEM_TYPE_FRIENDS_HEADER = 2;
    public static final int ITEM_TYPE_FRIENDS_FRIEND = 3;
    public static final int ITEM_TYPE_FACEBOOK_HEADER = 4;
    public static final int ITEM_TYPE_FACEBOOK_FRIEND = 5;
    private ImageLoader mImageLoader;
    private SortedList<Friend> friends;

    public FriendsAdapter(Context context, SortedList<Friend> friends) {
        this.friends = friends;

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

    public static class Friend {

        public String userId;
        public String avatar;
        public String name;
        public String username;
        public Integer type;

        public Friend(String userId, String avatar, String name, String username, Integer type) {
            this.userId = userId;
            this.avatar = avatar;
            this.name = name;
            this.username = username;
            this.type = type;
        }

        public Friend(String name, Integer type) {
            this.name = name;
            this.type = type;
        }

        public String getUserId() {
            return userId;
        }

        public String getAvatar() {
            return avatar;
        }

        public String getName() {
            return name;
        }

        public String getUsername() {
            return username;
        }

        public Integer getType() {
            return type;
        }

    }

    @Override
    public int getItemViewType(int position) {
        return friends.get(position).getType();
    }

    public class FriendHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView avatarImg;
        public TextView nameTxt;
        public TextView usernameTxt;
        public TextView timeTxt;

        public FriendHolder(View v) {
            super(v);
            avatarImg = (ImageView) v.findViewById(R.id.friends_img);
            nameTxt = (TextView) v.findViewById(R.id.friends_name_txt);
            usernameTxt = (TextView) v.findViewById(R.id.friends_username_txt);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            friendClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FriendHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        if (isItem(viewType)) {
            return new FriendHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_friends, parent, false));
        } else {
            return new FriendHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_friends_subheader, parent, false));
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(FriendHolder holder, int position) {
        Friend friend = friends.get(position);

        if (isItem(getItemViewType(position))) {
            mImageLoader.displayImage(friend.getAvatar(), holder.avatarImg);
            holder.usernameTxt.setText(friend.getUsername());
            holder.nameTxt.setText(friend.getName());
        } else {
            holder.nameTxt.setText(Html.fromHtml("<b>" + friend.getName() + "</b>"));
        }
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    OnItemClickListener friendClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(View v, int position) {
        }

    };

    private Boolean isItem(int type) {
        return (type == ITEM_TYPE_REQUESTS_FRIEND
                || type == ITEM_TYPE_FRIENDS_FRIEND
                || type == ITEM_TYPE_FACEBOOK_FRIEND);
    }
}
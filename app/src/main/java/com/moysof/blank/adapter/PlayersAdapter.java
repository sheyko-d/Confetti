package com.moysof.blank.adapter;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moysof.blank.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class PlayersAdapter extends
        RecyclerView.Adapter<PlayersAdapter.PlayerHolder> {

    private Context mContext;
    private SortedList<Player> players;
    public static final int ITEM_TYPE_SPACE = 0;
    public static final int ITEM_TYPE_PLAYER = 1;
    private ImageLoader mImageLoader;

    public PlayersAdapter(Context context, SortedList<Player> players) {
        mContext = context;
        this.players = players;

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

    public static class Player {

        public Integer type;
        public String playerId;
        public String name;
        public String username;
        public String avatar;
        public Integer teamColor;

        public Player(Integer type, String playerId, String name, String username, String avatar,
                      int teamColor) {
            this.type = type;
            this.playerId = playerId;
            this.name = name;
            this.username = username;
            this.avatar = avatar;
            this.teamColor = teamColor;
        }

        public Player(Integer type, int teamColor) {
            this.type = type;
            this.teamColor = teamColor;
        }

        public Integer getType() {
            return type;
        }

        public String getPlayerId() {
            return playerId;
        }

        public String getName() {
            return name;
        }

        public String getUsername() {
            return username;
        }

        public String getAvatar() {
            return avatar;
        }

        public Integer getTeamColor() {
            return teamColor;
        }

    }

    public class PlayerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public View layout;
        public ImageView avatarImg;
        public TextView nameTxt;
        public TextView usernameTxt;

        public PlayerHolder(View v) {
            super(v);
            layout = v.findViewById(R.id.lobby_player_layout);
            avatarImg = (ImageView) v.findViewById(R.id.lobby_player_img);
            nameTxt = (TextView) v.findViewById(R.id.lobby_player_name_txt);
            usernameTxt = (TextView) v.findViewById(R.id.lobby_player_username_txt);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            playerClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return players.get(position).getType();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PlayerHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        if (viewType == ITEM_TYPE_PLAYER) {
            return new PlayerHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_players, parent, false));
        } else {
            return new PlayerHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_players_space, parent, false));
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(PlayerHolder holder, int position) {
        Player player = players.get(position);

        if (getItemViewType(position) == ITEM_TYPE_PLAYER) {
            holder.nameTxt.setText(player.getName());
            holder.usernameTxt.setText("@" + player.getUsername());
            holder.layout.setBackgroundColor(player.getTeamColor());
            mImageLoader.displayImage(player.getAvatar(), holder.avatarImg);
        }
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    OnItemClickListener playerClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(View v, int position) {
        }

    };
}
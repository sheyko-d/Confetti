package com.moysof.whattheblank.adapter;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moysof.whattheblank.R;
import com.moysof.whattheblank.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class HostPlayersAdapter extends
        RecyclerView.Adapter<HostPlayersAdapter.PlayersHolder> {

    private ImageLoader mImageLoader;
    private Context mContext;
    private SortedList<Player> teams;

    public HostPlayersAdapter(Context context, SortedList<Player> teams) {
        mContext = context;
        this.teams = teams;

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(250, true, false, false))
                .showImageOnLoading(R.drawable.avatar_placeholder)
                .showImageOnFail(R.drawable.avatar_placeholder).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).defaultDisplayImageOptions(defaultOptions).build();
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(config);
    }

    public static class Player {

        public String playerId;
        public String name;
        public String username;
        public Integer teamColor;
        public String avatar;

        public Player(String playerId, String name, String username, Integer teamColor,
                      String avatar) {
            this.playerId = playerId;
            this.name = name;
            this.username = username;
            this.teamColor = teamColor;
            this.avatar = avatar;
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

        public Integer getTeamColor() {
            return teamColor;
        }

        public String getAvatar() {
            return avatar;
        }

    }

    public class PlayersHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView nameTxt;
        public TextView usernameTxt;
        public ImageView avatarImg;
        public View colorView;

        public PlayersHolder(View v) {
            super(v);
            nameTxt = (TextView) v.findViewById(R.id.host_team_name_txt);
            usernameTxt = (TextView) v.findViewById(R.id.host_team_username_txt);
            avatarImg = (ImageView) v.findViewById(R.id.host_team_img);
            colorView = v.findViewById(R.id.host_team_color_view);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            joinClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PlayersHolder onCreateViewHolder(ViewGroup parent,
                                            int viewType) {
        return new PlayersHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_host_player, parent, false));
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(PlayersHolder holder, int position) {
        Player player = teams.get(position);

        Util.Log("avatar = " + player.getAvatar());
        mImageLoader.displayImage(player.getAvatar(), holder.avatarImg);
        holder.nameTxt.setText(player.getName());
        holder.usernameTxt.setText("@" + player.getUsername());
        holder.colorView.setBackgroundColor(player.getTeamColor());
    }

    @Override
    public int getItemCount() {
        return teams.size();
    }

    OnItemClickListener joinClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(View v, int position) {
        }

    };
}
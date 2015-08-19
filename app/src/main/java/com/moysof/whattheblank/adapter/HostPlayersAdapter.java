package com.moysof.whattheblank.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.util.SortedList;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.moysof.whattheblank.HostLobbyGameFragment;
import com.moysof.whattheblank.HostLobbyPlayersFragment;
import com.moysof.whattheblank.R;
import com.moysof.whattheblank.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HostPlayersAdapter extends
        RecyclerView.Adapter<HostPlayersAdapter.PlayersHolder> {

    private HostLobbyPlayersFragment mPlayersFragment;
    private String mGameId;
    private ImageLoader mImageLoader;
    private Context mContext;
    private SortedList<Player> players;

    public HostPlayersAdapter(Context context, SortedList<Player> players, String gameId,
                              HostLobbyPlayersFragment playersFragment) {
        mContext = context;
        this.players = players;
        mGameId = gameId;
        mPlayersFragment = playersFragment;

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

        public String id;
        public String name;
        public String username;
        public String teamColorHex;
        public String avatar;

        public Player(String id, String name, String username, String teamColorHex,
                      String avatar) {
            this.id = id;
            this.name = name;
            this.username = username;
            this.teamColorHex = teamColorHex;
            this.avatar = avatar;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getUsername() {
            return username;
        }

        public String getTeamColorHex() {
            return teamColorHex;
        }

        public Integer getTeamColor() {
            return Color.parseColor("#" + teamColorHex);
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
            pickColorClickListener.onItemClick(v, getAdapterPosition());
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
        Player player = players.get(position);

        Util.Log("avatar = " + player.getAvatar());
        mImageLoader.displayImage(player.getAvatar(), holder.avatarImg);
        holder.nameTxt.setText(player.getName());
        holder.usernameTxt.setText("@" + player.getUsername());
        holder.colorView.setBackgroundColor(player.getTeamColor());
    }

    @Override
    public int getItemCount() {
        return players.size();
    }


    private AlertDialog mDialog;
    private View mProgressBar;
    private RecyclerView mRecyclerView;

    OnItemClickListener pickColorClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(View v, final int position) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext,
                    R.style.MaterialDialogStyle);
            dialogBuilder.setTitle("Change Player's Team");

            View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_team_color,
                    null);
            mRecyclerView = (RecyclerView) dialogView.findViewById
                    (R.id.colors_recycler_view);
            mProgressBar = dialogView.findViewById(R.id.colors_progress_bar);
            GridLayoutManager layoutManager = new GridLayoutManager(mContext, 4);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setHasFixedSize(true);

            int teamsCount = HostLobbyGameFragment.sTeams.size();
            ArrayList<String> teamColors = new ArrayList<>();
            for (int i = 0; i < teamsCount; i++) {
                teamColors.add(HostLobbyGameFragment.sTeams.get(i).getColorHex());
            }

            final ColorsAdapter adapter = new ColorsAdapter(mContext, teamColors,
                    new ArrayList<String>());
            adapter.setTeamColor(players.get(position).getTeamColorHex());
            mRecyclerView.setAdapter(adapter);

            dialogBuilder.setView(dialogView);
            dialogBuilder.setPositiveButton("OK", null);
            dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            mDialog = dialogBuilder.create();
            mDialog.show();
            mDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            changePlayerTeam(mGameId, players.get(position).getId(),
                                    adapter.getTeamColor());
                        }
                    });
        }
    };

    private void changePlayerTeam(final String gameId, final String playerId, final String color) {
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Util.URL_CHANGE_PLAYERS_TEAM, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseJSON = new JSONObject(response);
                    if (responseJSON.getString("result").equals("success")) {
                        mDialog.cancel();
                        HostLobbyGameFragment.getTeams();
                    } else if (responseJSON.getString("result").equals("empty")) {
                        Toast.makeText(mContext, "Some fields are empty",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mContext, "Unknown server error",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    if (Util.isDebugging()) {
                        Toast.makeText(mContext, "JSON error: " + response,
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mContext, "Unknown server error",
                                Toast.LENGTH_LONG).show();
                    }
                }

                Util.Log(response);
                mProgressBar.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, "Server error",
                        Toast.LENGTH_LONG).show();
                Util.Log("Server error: " + error);

                mProgressBar.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        }) {
            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                if (volleyError.networkResponse != null
                        && volleyError.networkResponse.data != null) {
                    volleyError = new VolleyError(new String(volleyError.networkResponse.data));
                }

                return volleyError;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("game_id", gameId);
                params.put("player_id", playerId);
                params.put("color", color);
                return params;
            }


        };
        // Add the request to the RequestQueue.
        Volley.newRequestQueue(mContext).add(stringRequest);
    }
}
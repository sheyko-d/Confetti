package com.moysof.whattheblank.adapter;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.moysof.whattheblank.JoinLobbyActivity;
import com.moysof.whattheblank.R;
import com.moysof.whattheblank.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JoinAdapter extends
        RecyclerView.Adapter<JoinAdapter.GameHolder> {

    private Context mContext;
    private SortedList<Game> games;
    public static final int ITEM_TYPE_GAME = 0;
    public static final int ITEM_TYPE_GAME_FRIEND = 1;

    public JoinAdapter(Context context, SortedList<Game> games) {
        mContext = context;
        this.games = games;
    }

    public static class Game {

        public Integer type;
        public String gameId;
        public String name;
        public String username;

        public Game(Integer type, String gameId, String name, String username) {
            this.type = type;
            this.gameId = gameId;
            this.name = name;
            this.username = username;
        }

        public Integer getType() {
            return type;
        }

        public String getGameId() {
            return gameId;
        }

        public String getName() {
            return name;
        }

        public String getUsername() {
            return username;
        }

    }

    public class GameHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView nameTxt;
        public TextView usernameTxt;
        public Button joinBtn;

        public GameHolder(View v) {
            super(v);
            nameTxt = (TextView) v.findViewById(R.id.join_game_name_txt);
            usernameTxt = (TextView) v.findViewById(R.id.join_game_username_txt);
            joinBtn = (Button) v.findViewById(R.id.join_game_btn);

            joinBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            joinClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return games.get(position).getType();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public GameHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        if (viewType == ITEM_TYPE_GAME) {
            return new GameHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_games, parent, false));
        } else {
            return new GameHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_games_friend, parent, false));
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(GameHolder holder, int position) {
        Game game = games.get(position);

        if (getItemViewType(position) == ITEM_TYPE_GAME) {
            holder.usernameTxt.setText("Created by @" + game.getUsername());
        } else {
            SpannableStringBuilder sb = new SpannableStringBuilder("Created by @"
                    + game.getUsername());
            final ForegroundColorSpan fcs = new ForegroundColorSpan(mContext.getResources()
                    .getColor(R.color.primary));
            sb.setSpan(fcs, 11, sb.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            holder.usernameTxt.setText(sb);
        }
        holder.nameTxt.setText(game.getName());
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    OnItemClickListener joinClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(View v, int position) {
            String gameId = games.get(position).getGameId();
            String name = games.get(position).getName();
            joinGame(gameId, name);
        }

    };

    private void joinGame(final String gameId, final String name) {
        final String id = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString("id", "");

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Util.URL_JOIN_GAME, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseJSON = new JSONObject(response);
                    if (responseJSON.getString("result").equals("success")) {
                        mContext.startActivity(new Intent(mContext, JoinLobbyActivity.class)
                                .putExtra(JoinLobbyActivity.EXTRA_TITLE, name));
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
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, "Server error",
                        Toast.LENGTH_LONG).show();
                Util.Log("Server error: " + error);
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
                params.put("user_id", id);
                params.put("game_id", gameId);
                return params;
            }


        };
        // Add the request to the RequestQueue.
        Volley.newRequestQueue(mContext).add(stringRequest);
    }
}
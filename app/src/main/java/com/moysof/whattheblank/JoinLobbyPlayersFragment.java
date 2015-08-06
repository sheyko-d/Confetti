package com.moysof.whattheblank;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.moysof.whattheblank.adapter.PlayersAdapter;
import com.moysof.whattheblank.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JoinLobbyPlayersFragment extends Fragment {
    private static String sGameId;
    private PlayersAdapter mAdapter;
    private SortedList<PlayersAdapter.Player> mPlayers = new SortedList<>(PlayersAdapter.Player
            .class, new SortedList.Callback<PlayersAdapter.Player>() {
        @Override
        public int compare(PlayersAdapter.Player o1, PlayersAdapter.Player o2) {
            int i = o1.getTeamColor().compareTo(o2.getTeamColor());
            if (i != 0) return i;

            return o1.getType().compareTo(o2.getType());
        }

        @Override
        public void onInserted(int position, int count) {
            mAdapter.notifyItemRangeInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            mAdapter.notifyItemRangeRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            mAdapter.notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onChanged(int position, int count) {
            mAdapter.notifyItemRangeChanged(position, count);
        }

        @Override
        public boolean areContentsTheSame(PlayersAdapter.Player oldItem, PlayersAdapter
                .Player newItem) {
            // return whether the items' visual representations are the same or not.
            return oldItem.getType().equals(newItem.getType()) && oldItem.getName()
                    .equals(newItem.getName()) && oldItem.getUsername()
                    .equalsIgnoreCase(oldItem.getUsername());
        }

        @Override
        public boolean areItemsTheSame(PlayersAdapter.Player item1, PlayersAdapter.Player
                item2) {
            return item1.getType().equals(item2.getType()) && item1.getPlayerId()
                    .equals(item2.getPlayerId());
        }
    });

    public static JoinLobbyPlayersFragment newInstance(String gameId) {
        sGameId = gameId;

        return new JoinLobbyPlayersFragment();
    }

    public JoinLobbyPlayersFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_join_lobby_players, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView
                .findViewById(R.id.lobby_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new PlayersAdapter(getActivity(), mPlayers);
        recyclerView.setAdapter(mAdapter);

        getPlayers();

        return rootView;
    }

    public void getPlayers() {
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Util.URL_GET_PLAYERS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Util.Log(response);
                try {
                    JSONObject responseJSON = new JSONObject(response);
                    if (responseJSON.getString("result").equals("success")) {
                        JSONArray playersJSON = responseJSON.getJSONArray("players");
                        mPlayers.beginBatchedUpdates();
                        mPlayers.clear();
                        int playersCount = playersJSON.length();
                        ArrayList<Integer> teamColors = new ArrayList<>();
                        for (int i = 0; i < playersCount; i++) {
                            String playerId = playersJSON.getJSONObject(i).getString("player_id");
                            String name = playersJSON.getJSONObject(i).getString("name");
                            String username = playersJSON.getJSONObject(i).getString("username");
                            String avatar = playersJSON.getJSONObject(i).getString("avatar");
                            int color = Color.parseColor("#" + playersJSON.getJSONObject(i)
                                    .getString("color"));
                            mPlayers.add(new PlayersAdapter.Player(PlayersAdapter.ITEM_TYPE_PLAYER,
                                    playerId, name, username, avatar, color));

                            if (!teamColors.contains(color)) {
                                teamColors.add(color);
                            }
                        }

                        int colorsCount = teamColors.size();
                        for (int i = 0; i < colorsCount; i++) {
                            mPlayers.add(new PlayersAdapter.Player(PlayersAdapter.ITEM_TYPE_SPACE,
                                    teamColors.get(i)));
                        }

                        mPlayers.endBatchedUpdates();
                    } else if (responseJSON.getString("result").equals("empty")) {
                        Toast.makeText(getActivity(), "Some fields are empty",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "Unknown server error",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    if (Util.isDebugging()) {
                        Toast.makeText(getActivity(), "JSON error: " + response,
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "Unknown server error",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Server error",
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
                params.put("game_id", sGameId);
                return params;
            }


        };
        // Add the request to the RequestQueue.
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

}
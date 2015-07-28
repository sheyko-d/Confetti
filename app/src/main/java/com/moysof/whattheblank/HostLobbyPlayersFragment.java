package com.moysof.whattheblank;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.moysof.whattheblank.adapter.HostPlayersAdapter;
import com.moysof.whattheblank.view.EmptyRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HostLobbyPlayersFragment extends Fragment {

    private static RequestQueue sQueue;
    private static String sGameId;
    private static int sNumberTeams;
    private static int sNumberPlayers;
    private HostPlayersAdapter mAdapter;
    private Button mPlayersBtn;
    private AlertDialog mDialog;
    private TextInputLayout mNameLayout;
    private View mAddProgressBar;
    private SortedList<HostPlayersAdapter.Player> mPlayers = new SortedList<>(HostPlayersAdapter
            .Player.class, new SortedList.Callback<HostPlayersAdapter.Player>() {
        @Override
        public int compare(HostPlayersAdapter.Player o1, HostPlayersAdapter.Player o2) {
            return o1.getTeamColor().compareTo(o2.getTeamColor());
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
        public boolean areContentsTheSame(HostPlayersAdapter.Player oldItem,
                                          HostPlayersAdapter.Player newItem) {
            // return whether the items' visual representations are the same or not.
            return oldItem.getName().equals(newItem.getName()) && oldItem
                    .getUsername().equals(newItem.getUsername())
                    && oldItem.getTeamColor().equals(oldItem.getTeamColor());
        }

        @Override
        public boolean areItemsTheSame(HostPlayersAdapter.Player item1,
                                       HostPlayersAdapter.Player item2) {
            return item1.getPlayerId().equals(item2.getPlayerId());
        }
    });

    public static HostLobbyPlayersFragment newInstance(String gameId, int numberTeams,
                                                       int numberPlayers, RequestQueue queue) {
        sGameId = gameId;
        sNumberTeams = numberTeams;
        sNumberPlayers = numberPlayers;
        sQueue = queue;

        return new HostLobbyPlayersFragment();
    }

    public HostLobbyPlayersFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_host_lobby_players, container, false);

        mPlayersBtn = (Button) rootView.findViewById(R.id.players_btn);
        mPlayersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialog();
            }
        });

        EmptyRecyclerView recyclerView = (EmptyRecyclerView) rootView
                .findViewById(R.id.host_lobby_players_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setEmptyView(rootView.findViewById(R.id.host_players_placeholder_layout));

        mAdapter = new HostPlayersAdapter(getActivity(), mPlayers);
        recyclerView.setAdapter(mAdapter);

        getPlayers();

        return rootView;
    }

    private void showAddDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(),
                R.style.MaterialDialogStyle);
        dialogBuilder.setTitle("Manually Add");
        View dialogView
                = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_player, null);
        final EditText nameEditTxt = (EditText) dialogView
                .findViewById(R.id.host_players_add_edit_txt);
        mAddProgressBar = dialogView.findViewById(R.id.host_players_add_progress_bar);
        mNameLayout = (TextInputLayout) dialogView
                .findViewById(R.id.host_players_add_layout);
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
                        String name = nameEditTxt.getText().toString();
                        if (TextUtils.isEmpty(name)) {
                            mNameLayout.setError("Player's name is required");
                        } else {
                            mNameLayout.setError(null);
                            addPlayer(name);
                        }
                    }
                });
    }

    private void addPlayer(final String name) {
        mAddProgressBar.setVisibility(View.VISIBLE);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Util.URL_ADD_PLAYER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Util.Log(response);
                try {
                    mAddProgressBar.setVisibility(View.GONE);
                    JSONObject responseJSON = new JSONObject(response);
                    if (responseJSON.getString("result").equals("success")) {
                        mDialog.cancel();
                        HostLobbyGameFragment.sForceUpdate = true;
                        getPlayers();
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
                mAddProgressBar.setVisibility(View.GONE);
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
                params.put("name", name);
                params.put("game_id", sGameId + "");
                return params;
            }


        };
        // Add the request to the RequestQueue.
        sQueue.add(stringRequest);
    }

    private void getPlayers() {
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
                        int playersCount = playersJSON.length();
                        mPlayers.beginBatchedUpdates();
                        mPlayers.clear();
                        for (int i = 0; i < playersCount; i++) {
                            String playerId = playersJSON.getJSONObject(i).getString("player_id");
                            String name = playersJSON.getJSONObject(i).getString("name");
                            String username = playersJSON.getJSONObject(i).getString("username");
                            String avatar = playersJSON.getJSONObject(i).getString("avatar");
                            String teamId = playersJSON.getJSONObject(i).getString("team_id");
                            int color = Color.parseColor("#" + playersJSON.getJSONObject(i)
                                    .getString("color"));
                            mPlayers.add(new HostPlayersAdapter.Player(playerId, name, username,
                                    color, avatar));
                        }
                        mPlayers.endBatchedUpdates();

                        mPlayersBtn.setEnabled(playersCount < sNumberPlayers * sNumberTeams);
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
                params.put("game_id", sGameId + "");
                return params;
            }


        };
        // Add the request to the RequestQueue.
        sQueue.add(stringRequest);
    }

}
package com.moysof.whattheblank;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.moysof.whattheblank.adapter.HostTeamsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HostLobbyGameFragment extends Fragment {

    private static RequestQueue sQueue;
    private static HostTeamsAdapter mAdapter;
    private static String sGameId;
    private static String sPassword;
    private static Integer sNumberTeams;
    public static Integer sNumberPlayers;
    private static Integer sNumberCards;
    private static Integer sNumberTime;
    public static boolean sForceUpdate = false;
    private static Button sHostBtn;
    private static HostLobbyGameFragment sFragment;
    public static SortedList<HostTeamsAdapter.Team> sTeams = new SortedList<>(HostTeamsAdapter.Team
            .class, new SortedList.Callback<HostTeamsAdapter.Team>() {
                @Override
                public int compare(HostTeamsAdapter.Team o1, HostTeamsAdapter.Team o2) {
                    return o1.getNumber().compareTo(o2.getNumber());
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
                public boolean areContentsTheSame(HostTeamsAdapter.Team oldItem,
                                                  HostTeamsAdapter.Team newItem) {
                    // return whether the items' visual representations are the same or not.
                    return oldItem.getNumber().equals(newItem.getNumber()) && oldItem
                            .getAssignedCount().equals(newItem.getAssignedCount())
                            && oldItem.getColor().equals(oldItem.getColor());
                }

                @Override
                public boolean areItemsTheSame(HostTeamsAdapter.Team item1,
                                               HostTeamsAdapter.Team item2) {
                    return item1.getNumber().equals(item2.getNumber());
                }
            });

    public static HostLobbyGameFragment newInstance(String gameId, String password,
                                                    Integer numberTeams, Integer numberPlayers,
                                                    Integer numberCards, Integer numberTime,
                                                    RequestQueue queue) {

        sGameId = gameId;
        sPassword = password;
        sNumberTeams = numberTeams;
        sNumberPlayers = numberPlayers;
        sNumberCards = numberCards;
        sNumberTime = numberTime;
        sQueue = queue;

        return new HostLobbyGameFragment();
    }

    public HostLobbyGameFragment() {
        sFragment = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_host_lobby_game, container, false);

        sHostBtn = (Button) rootView.findViewById(R.id.host_btn);

        RecyclerView recyclerView = (RecyclerView) rootView
                .findViewById(R.id.host_lobby_game_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ((TextView) rootView.findViewById(R.id.host_lobby_id_txt)).setText(sGameId + "");
        ((TextView) rootView.findViewById(R.id.host_lobby_password_txt)).setText(sPassword + "");

        mAdapter = new HostTeamsAdapter(getActivity(), sTeams, sNumberPlayers, sGameId, this);
        recyclerView.setAdapter(mAdapter);

        getTeams();

        return rootView;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible && sForceUpdate) {
            getTeams();
            sForceUpdate = false;
        }
    }

    public static void getTeams() {
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Util.URL_GET_TEAMS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Util.Log(response);
                try {
                    JSONObject responseJSON = new JSONObject(response);
                    if (responseJSON.getString("result").equals("success")) {
                        JSONArray teamsJSON = responseJSON.getJSONArray("teams");
                        int teamsCount = teamsJSON.length();
                        sTeams.beginBatchedUpdates();
                        sTeams.clear();
                        for (int i = 0; i < teamsCount; i++) {
                            String id = teamsJSON.getJSONObject(i).getString("team_id");
                            int number = teamsJSON.getJSONObject(i).getInt("number");
                            int count = teamsJSON.getJSONObject(i).getInt("count");
                            String colorHex = teamsJSON.getJSONObject(i)
                                    .getString("color");
                            sTeams.add(new HostTeamsAdapter.Team(id, number, count, colorHex));
                        }
                        sTeams.endBatchedUpdates();
                    } else if (responseJSON.getString("result").equals("empty")) {
                        Toast.makeText(sFragment.getActivity(), "Some fields are empty",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(sFragment.getActivity(), "Unknown server error",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    if (Util.isDebugging()) {
                        Toast.makeText(sFragment.getActivity(), "JSON error: " + response,
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(sFragment.getActivity(), "Unknown server error",
                                Toast.LENGTH_LONG).show();
                    }
                }

                sHostBtn.setEnabled(HostLobbyPlayersFragment.sPlayers.size() >= sNumberTeams
                        * sNumberPlayers);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(sFragment.getActivity(), "Server error",
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
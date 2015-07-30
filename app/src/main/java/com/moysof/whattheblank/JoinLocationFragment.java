package com.moysof.whattheblank;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.moysof.whattheblank.adapter.JoinAdapter;
import com.moysof.whattheblank.view.EmptyRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JoinLocationFragment extends Fragment {
    private static RequestQueue sQueue;
    private JoinAdapter mAdapter;
    private TextView mLoadingTxt;
    private SwipeRefreshLayout mRefreshLayout;
    private String mLat;
    private String mLng;
    private SortedList<JoinAdapter.Game> mGames = new SortedList<>(JoinAdapter.Game.class,
            new SortedList.Callback<JoinAdapter.Game>() {
                @Override
                public int compare(JoinAdapter.Game o1, JoinAdapter.Game o2) {
                    return o1.getGameId().compareTo(o2.getGameId());
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
                public boolean areContentsTheSame(JoinAdapter.Game oldItem, JoinAdapter.Game newItem) {
                    // return whether the items' visual representations are the same or not.
                    return oldItem.getType().equals(newItem.getType()) && oldItem.getName()
                            .equals(newItem.getName()) && oldItem.getUsername()
                            .equalsIgnoreCase(oldItem.getUsername());
                }

                @Override
                public boolean areItemsTheSame(JoinAdapter.Game item1, JoinAdapter.Game item2) {
                    return item1.getGameId().equals(item2.getGameId());
                }
            });

    public static JoinLocationFragment newInstance(RequestQueue queue) {
        sQueue = queue;

        return new JoinLocationFragment();
    }

    public JoinLocationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_join_location, container, false);

        EmptyRecyclerView recyclerView = (EmptyRecyclerView) rootView
                .findViewById(R.id.join_location_recycler_view);
        mLoadingTxt = (TextView) rootView.findViewById(R.id.join_location_loading_txt);
        mRefreshLayout = (SwipeRefreshLayout) rootView
                .findViewById(R.id.join_location_refresh_layout);
        initRefreshLayout();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setEmptyView(rootView.findViewById(R.id.join_location_placeholder_layout));

        mAdapter = new JoinAdapter(getActivity(), mGames);
        recyclerView.setAdapter(mAdapter);

        return rootView;
    }

    private void initRefreshLayout() {
        mRefreshLayout.setColorSchemeResources(R.color.primary, R.color.green, R.color.red,
                R.color.blue);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mLat != null && mLng != null) {
                    searchGames(mLat, mLng);
                } else {
                    Toast.makeText(getActivity(), "Can't get your location", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    public void searchGames(final String lat, final String lng) {
        mLat = lat;
        mLng = lng;

        final String id = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString("id", "");

        mLoadingTxt.setText(R.string.join_title_location);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Util.URL_GET_GAMES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseJSON = new JSONObject(response);
                    if (responseJSON.getString("result").equals("success")) {
                        JSONArray gamesJSON = responseJSON.getJSONArray("games");
                        int gamesCount = gamesJSON.length();
                        mGames.clear();
                        mGames.beginBatchedUpdates();
                        for (int i = 0; i < gamesCount; i++) {
                            int type = gamesJSON.getJSONObject(i).getInt("type");
                            String gameId = gamesJSON.getJSONObject(i).getString("game_id");
                            String hostId = gamesJSON.getJSONObject(i).getString("host_id");
                            String name = gamesJSON.getJSONObject(i).getString("name");
                            String username = gamesJSON.getJSONObject(i).getString("username");
                            String password = gamesJSON.getJSONObject(i).getString("pwd");
                            String teamsMax = gamesJSON.getJSONObject(i).getString("teams_max");
                            String playersMax = gamesJSON.getJSONObject(i)
                                    .getString("players_max");
                            String cardsMax = gamesJSON.getJSONObject(i).getString("cards_max");
                            String time = gamesJSON.getJSONObject(i).getString("time");
                            mGames.add(new JoinAdapter.Game(type, gameId, name, username));
                        }
                        mGames.endBatchedUpdates();

                        mLoadingTxt.setText("Found " + mGames.size() + " games near your location");
                    } else if (responseJSON.getString("result").equals("empty")) {
                        Toast.makeText(getActivity(), "Some fields are empty",
                                Toast.LENGTH_LONG).show();
                        mLoadingTxt.setText("Can't find games");
                    } else {
                        Toast.makeText(getActivity(), "Unknown server error",
                                Toast.LENGTH_LONG).show();
                        mLoadingTxt.setText("Can't find games");
                    }
                } catch (JSONException e) {
                    if (Util.isDebugging()) {
                        Toast.makeText(getActivity(), "JSON error: " + response,
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "Unknown server error",
                                Toast.LENGTH_LONG).show();
                    }
                    mLoadingTxt.setText("Can't find games");
                }
                Util.Log(response);
                mRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Server error",
                        Toast.LENGTH_LONG).show();
                Util.Log("Server error: " + error);
                mLoadingTxt.setText("Can't find games");
                mRefreshLayout.setRefreshing(false);
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
                params.put("lat", lat);
                params.put("lng", lng);
                return params;
            }


        };
        // Add the request to the RequestQueue.
        sQueue.add(stringRequest);
    }

}
package com.moysof.whattheblank;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

public class JoinIDFragment extends Fragment {

    private static RequestQueue sQueue;
    private JoinAdapter mAdapter;
    private EditText mIdEditTxt;
    private View mProgressBar;
    private SwipeRefreshLayout mRefreshLayout;
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

    public static JoinIDFragment newInstance(RequestQueue queue) {
        sQueue = queue;

        return new JoinIDFragment();
    }

    public JoinIDFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_join_id, container, false);

        mIdEditTxt = (EditText) rootView.findViewById(R.id.join_id_edit_txt);
        mProgressBar = rootView.findViewById(R.id.join_id_progressbar);
        mRefreshLayout = (SwipeRefreshLayout) rootView
                .findViewById(R.id.join_id_refresh_layout);

        mIdEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchGames();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        initRefreshLayout();

        EmptyRecyclerView recyclerView = (EmptyRecyclerView) rootView
                .findViewById(R.id.join_id_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setEmptyView(rootView.findViewById(R.id.join_id_placeholder_layout));

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
                searchGames();
            }
        });
    }

    private void searchGames() {
        final String idQuery = mIdEditTxt.getText().toString();

        sQueue.cancelAll(Util.SEARCH_ID_REQUESTS);

        if (!TextUtils.isEmpty(idQuery)) {
            mProgressBar.setVisibility(View.VISIBLE);

            final String id = PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .getString("id", "");

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
                    Util.Log(response);

                    mProgressBar.setVisibility(View.GONE);
                    mRefreshLayout.setRefreshing(false);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), "Server error",
                            Toast.LENGTH_LONG).show();
                    Util.Log("Server error: " + error);

                    mProgressBar.setVisibility(View.GONE);
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
                    params.put("game_id", idQuery);
                    return params;
                }
            };

            stringRequest.setTag(Util.SEARCH_ID_REQUESTS);

            // Add the request to the RequestQueue.
            sQueue.add(stringRequest);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mGames.clear();
        }
    }
}
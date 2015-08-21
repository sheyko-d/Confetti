package com.moysof.confetti;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.moysof.confetti.adapter.FriendsAdapter;
import com.moysof.confetti.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FriendsFragment extends Fragment {
    private RecyclerView mRecycler;
    private LinearLayoutManager mLayoutManager;
    private FriendsAdapter mAdapter;
    private SwipeRefreshLayout mRefreshLayout;
    private final static String TYPE_FRIEND_PENDING = "0";
    private final static String TYPE_FRIEND_ACTIVE = "1";
    private RequestQueue mQueue;
    private SortedList<FriendsAdapter.Friend> mFriends = new SortedList<>
            (FriendsAdapter.Friend.class, new SortedList.Callback<FriendsAdapter.Friend>() {
                @Override
                public int compare(FriendsAdapter.Friend o1, FriendsAdapter.Friend o2) {
                    // Sort items, new ones first
                    int i = o1.getType().compareTo(o2.getType());
                    if (i != 0) return i;

                    if (o1.getName() != null && o2.getName() != null) {
                        return o1.getName().compareTo(o2.getName());
                    } else {
                        return 0;
                    }
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
                public boolean areContentsTheSame(FriendsAdapter.Friend oldItem,
                                                  FriendsAdapter.Friend newItem) {
                    // return whether the items' visual representations are the same or not.
                    return oldItem.getType().equals(newItem.getType()) && oldItem.getName()
                            .equalsIgnoreCase(oldItem.getName());
                }

                @Override
                public boolean areItemsTheSame(FriendsAdapter.Friend item1,
                                               FriendsAdapter.Friend item2) {
                    try {
                        return item1.getUserId().equals(item2.getUserId());
                    } catch (Exception e) {
                        return false;
                    }
                }
            });

    public static FriendsFragment newInstance() {
        FriendsFragment fragment = new FriendsFragment();
        return fragment;
    }

    public FriendsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        mRecycler = (RecyclerView) rootView.findViewById(R.id.friends_recycler_view);
        mRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.friends_refresh_layout);

        initRecycler();
        initRefreshLayout();

        return rootView;
    }

    private void initRecycler() {
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecycler.setLayoutManager(mLayoutManager);

        // Instantiate the RequestQueue.
        mQueue = Volley.newRequestQueue(getActivity());
        mAdapter = new FriendsAdapter((MainActivity) getActivity(), this, mQueue, mFriends);
        mRecycler.setAdapter(mAdapter);

        loadFriends();
    }

    public void loadFriends() {
        final String id = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString("id", "");

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Util.URL_GET_FRIENDS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseJSON = new JSONObject(response);
                            if (responseJSON.getString("result").equals("success")) {
                                JSONArray friendsJSON = responseJSON.getJSONArray("friends");

                                int friendsCount = friendsJSON.length();
                                mFriends.beginBatchedUpdates();
                                mFriends.clear();
                                Boolean containsPendingHeader = false;
                                for (int i = 0; i < friendsCount; i++) {
                                    String id = friendsJSON.getJSONObject(i).getString("id");
                                    String friendId = friendsJSON.getJSONObject(i)
                                            .getString("friend_id");
                                    String name = friendsJSON.getJSONObject(i).getString("name");
                                    String username = friendsJSON.getJSONObject(i)
                                            .getString("username");
                                    String avatar = friendsJSON.getJSONObject(i)
                                            .getString("avatar");
                                    String status = friendsJSON.getJSONObject(i)
                                            .getString("status");
                                    Boolean isFacebook = friendsJSON.getJSONObject(i)
                                            .getBoolean("is_facebook");
                                    if (status.equals(TYPE_FRIEND_PENDING)) {
                                        mFriends.add(new FriendsAdapter.Friend(id, friendId, avatar,
                                                name, username, FriendsAdapter
                                                .ITEM_TYPE_REQUESTS_FRIEND));
                                        if (!containsPendingHeader) {
                                            mFriends.add(new FriendsAdapter.Friend(getString
                                                    (R.string.friends_requests_header),
                                                    FriendsAdapter.ITEM_TYPE_REQUESTS_HEADER));
                                            containsPendingHeader = true;
                                        }
                                    } else if (isFacebook) {
                                        mFriends.add(new FriendsAdapter.Friend(id, friendId, avatar,
                                                name, username, FriendsAdapter
                                                .ITEM_TYPE_FACEBOOK_FRIEND));
                                    } else {
                                        mFriends.add(new FriendsAdapter.Friend(id, friendId, avatar,
                                                name, username,
                                                FriendsAdapter.ITEM_TYPE_FRIENDS_FRIEND));

                                    }
                                }

                                mFriends.add(new FriendsAdapter.Friend(getString
                                        (R.string.friends_friends_header),
                                        FriendsAdapter.ITEM_TYPE_FRIENDS_HEADER));
                                mFriends.add(new FriendsAdapter.Friend(FriendsAdapter
                                        .ITEM_TYPE_FRIENDS_ADD_BUTTON));

                                mFriends.add(new FriendsAdapter.Friend(getString
                                        (R.string.friends_facebook_header),
                                        FriendsAdapter.ITEM_TYPE_FACEBOOK_HEADER));
                                mFriends.add(new FriendsAdapter.Friend(FriendsAdapter
                                        .ITEM_TYPE_FACEBOOK_ADD_BUTTON));

                                mFriends.endBatchedUpdates();
                            } else {
                                Util.Log("Unknown server error");
                                Toast.makeText(getActivity(), "Unknown server error",
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Util.Log("JSON error: " + e);
                            Toast.makeText(getActivity(), "JSON error: " + e,
                                    Toast.LENGTH_LONG).show();
                        }
                        Util.Log(response);
                        mRefreshLayout.setRefreshing(false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Util.Log("Server error: " + error);
                Toast.makeText(getActivity(), "Server error: " + error, Toast.LENGTH_LONG).show();
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
                return params;
            }
        };
        // Add the request to the RequestQueue.
        mQueue.add(stringRequest);
    }

    private void initRefreshLayout() {
        mRefreshLayout.setColorSchemeResources(R.color.primary, R.color.green, R.color.red,
                R.color.blue);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadFriends();
            }
        });
    }
}
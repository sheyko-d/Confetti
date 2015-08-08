package com.moysof.whattheblank.adapter;

import android.content.DialogInterface;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.moysof.whattheblank.FriendsFragment;
import com.moysof.whattheblank.MainActivity;
import com.moysof.whattheblank.R;
import com.moysof.whattheblank.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FriendsAdapter extends
        RecyclerView.Adapter<FriendsAdapter.FriendHolder> {

    public static final int ITEM_TYPE_REQUESTS_HEADER = 0;
    public static final int ITEM_TYPE_REQUESTS_FRIEND = 1;
    public static final int ITEM_TYPE_FRIENDS_HEADER = 2;
    public static final int ITEM_TYPE_FRIENDS_FRIEND = 3;
    public static final int ITEM_TYPE_FRIENDS_ADD_BUTTON = 4;
    public static final int ITEM_TYPE_FACEBOOK_HEADER = 5;
    public static final int ITEM_TYPE_FACEBOOK_FRIEND = 6;
    public static final int ITEM_TYPE_FACEBOOK_ADD_BUTTON = 7;
    private RequestQueue mQueue;
    private FriendsFragment mFragment;
    private MainActivity mActivity;
    private ImageLoader mImageLoader;
    private SortedList<Friend> friends;
    private SortedList<InviteFriendsAdapter.Friend> mInviteFriends
            = new SortedList<>
            (InviteFriendsAdapter.Friend.class, new SortedList.Callback<InviteFriendsAdapter.Friend>() {
                @Override
                public int compare(InviteFriendsAdapter.Friend o1, InviteFriendsAdapter.Friend o2) {
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
                public boolean areContentsTheSame(InviteFriendsAdapter.Friend oldItem,
                                                  InviteFriendsAdapter.Friend newItem) {
                    // return whether the items' visual representations are the same or not.
                    return oldItem.getName().equalsIgnoreCase(oldItem.getName());
                }

                @Override
                public boolean areItemsTheSame(InviteFriendsAdapter.Friend item1,
                                               InviteFriendsAdapter.Friend item2) {
                    try {
                        return item1.getUserId().equals(item2.getUserId());
                    } catch (Exception e) {
                        return false;
                    }
                }
            });
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private InviteFriendsAdapter mAdapter;
    private String mMyId;

    public FriendsAdapter(MainActivity activity, FriendsFragment fragment, RequestQueue queue,
                          SortedList<Friend> friends) {
        this.friends = friends;
        mActivity = activity;
        mFragment = fragment;
        mQueue = queue;

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true).showImageOnFail(R.drawable.avatar_placeholder)
                .displayer(new FadeInBitmapDisplayer(250, true, false, false))
                .showImageOnLoading(android.R.color.white)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                activity).defaultDisplayImageOptions(defaultOptions).build();
        ImageLoader.getInstance().init(config);
        mImageLoader = ImageLoader.getInstance();

        mMyId = PreferenceManager.getDefaultSharedPreferences(activity).getString("id", "");
    }

    public static class Friend {

        public String userId;
        public String friendId;
        public String avatar;
        public String name;
        public String username;
        public Integer type;

        public Friend(String userId, String friendId, String avatar, String name, String username, Integer type) {
            this.userId = userId;
            this.friendId = friendId;
            this.avatar = avatar;
            this.name = name;
            this.username = username;
            this.type = type;
        }

        public Friend(String name, Integer type) {
            this.name = name;
            this.type = type;
        }

        public Friend(Integer type) {
            this.type = type;
        }

        public String getUserId() {
            return userId;
        }

        public String getFriendId() {
            return friendId;
        }

        public String getAvatar() {
            return avatar;
        }

        public String getName() {
            return name;
        }

        public String getUsername() {
            return username;
        }

        public Integer getType() {
            return type;
        }

    }

    @Override
    public int getItemViewType(int position) {
        return friends.get(position).getType();
    }

    public class FriendHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView avatarImg;
        public TextView nameTxt;
        public TextView usernameTxt;
        public TextView timeTxt;
        public Button facebookBtn;
        public Button friendsBtn;
        private ImageButton addBtn;

        public FriendHolder(View v) {
            super(v);
            avatarImg = (ImageView) v.findViewById(R.id.friends_img);
            nameTxt = (TextView) v.findViewById(R.id.friends_name_txt);
            usernameTxt = (TextView) v.findViewById(R.id.friends_username_txt);
            facebookBtn = (Button) v.findViewById(R.id.friends_facebook_btn);
            friendsBtn = (Button) v.findViewById(R.id.friends_btn);
            addBtn = (ImageButton) v.findViewById(R.id.friends_add_btn);

            if (facebookBtn != null) {
                facebookBtn.setOnClickListener(this);
            } else if (friendsBtn != null) {
                friendsBtn.setOnClickListener(this);
            } else if (addBtn != null) {
                addBtn.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.friends_facebook_btn || v.getId() == R.id.friends_btn) {
                inviteClickListener.onItemClick(v, getAdapterPosition());
            } else {
                addClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FriendHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        if (viewType == ITEM_TYPE_REQUESTS_FRIEND) {
            return new FriendHolder(LayoutInflater.from(mActivity).inflate(
                    R.layout.item_friends_pending, parent, false));
        } else if (viewType == ITEM_TYPE_FRIENDS_FRIEND || viewType == ITEM_TYPE_FACEBOOK_FRIEND) {
            return new FriendHolder(LayoutInflater.from(mActivity).inflate(
                    R.layout.item_friends, parent, false));
        } else if (viewType == ITEM_TYPE_FACEBOOK_ADD_BUTTON) {
            return new FriendHolder(LayoutInflater.from(mActivity).inflate(
                    R.layout.item_friends_facebook_add, parent, false));
        } else if (viewType == ITEM_TYPE_FRIENDS_ADD_BUTTON) {
            return new FriendHolder(LayoutInflater.from(mActivity).inflate(
                    R.layout.item_friends_add, parent, false));
        } else {
            return new FriendHolder(LayoutInflater.from(mActivity).inflate(
                    R.layout.item_friends_subheader, parent, false));
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(FriendHolder holder, int position) {
        Friend friend = friends.get(position);

        if (isItem(getItemViewType(position))) {
            mImageLoader.displayImage(friend.getAvatar(), holder.avatarImg);
            holder.usernameTxt.setText("@" + friend.getUsername());
            holder.nameTxt.setText(friend.getName());

            if (holder.addBtn != null) {
                holder.addBtn.setVisibility(friends.get(position).getFriendId().equals(mMyId)
                        ? View.VISIBLE : View.GONE);
            }
        } else if (getItemViewType(position) != ITEM_TYPE_FACEBOOK_ADD_BUTTON
                && getItemViewType(position) != ITEM_TYPE_FRIENDS_ADD_BUTTON) {
            holder.nameTxt.setText(Html.fromHtml("<b>" + friend.getName() + "</b>"));
        }
    }

    private Boolean isItem(int type) {
        return (type == ITEM_TYPE_REQUESTS_FRIEND
                || type == ITEM_TYPE_FRIENDS_FRIEND
                || type == ITEM_TYPE_FACEBOOK_FRIEND);
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    OnItemClickListener inviteClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(View v, int position) {
            if (v.getId() == R.id.friends_facebook_btn) {
                mActivity.inviteFriends();
            } else {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity,
                        R.style.MaterialDialogStyle);
                dialogBuilder.setTitle("Find Friends");
                View dialogView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_friends,
                        null);
                mProgressBar = (ProgressBar) dialogView
                        .findViewById(R.id.invite_progress_bar);
                mRecyclerView = (RecyclerView) dialogView
                        .findViewById(R.id.invite_recycler_view);
                LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
                mRecyclerView.setLayoutManager(layoutManager);
                loadInviteFriends();
                mAdapter = new InviteFriendsAdapter(mActivity, mFragment, mQueue,
                        mInviteFriends, FriendsAdapter.this);
                mRecyclerView.setAdapter(mAdapter);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialogBuilder.create().show();
            }
        }

    };

    OnItemClickListener addClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(View v, int position) {
            updateFriendStatus(friends.get(position).getUserId(), 1);
        }
    };

    private void updateFriendStatus(final String friendId, final int status) {
        final String id = PreferenceManager.getDefaultSharedPreferences(mActivity)
                .getString("id", "");

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Util.URL_UPDATE_FRIEND_STATUS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseJSON = new JSONObject(response);
                    if (responseJSON.getString("result").equals("success")) {
                    } else {
                        Util.Log("Unknown server error");
                        Toast.makeText(mActivity, "Unknown server error",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Util.Log("JSON error: " + e);
                    Toast.makeText(mActivity, "JSON error: " + e,
                            Toast.LENGTH_LONG).show();
                }
                Util.Log(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Util.Log("Server error: " + error);
                Toast.makeText(mActivity, "Server error: " + error, Toast.LENGTH_LONG).show();
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
                params.put("friend_id", friendId);
                params.put("status", status + "");
                return params;
            }
        };
        // Add the request to the RequestQueue.
        mQueue.add(stringRequest);

        mFragment.loadFriends();
    }

    public void loadInviteFriends() {
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);

        final String id = PreferenceManager.getDefaultSharedPreferences(mActivity)
                .getString("id", "");

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Util.URL_GET_INVITE_FRIENDS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseJSON = new JSONObject(response);
                    if (responseJSON.getString("result").equals("success")) {
                        JSONArray friendsJSON = responseJSON.getJSONArray("friends");

                        int friendsCount = friendsJSON.length();
                        mInviteFriends.beginBatchedUpdates();
                        mInviteFriends.clear();
                        for (int i = 0; i < friendsCount; i++) {
                            String id = friendsJSON.getJSONObject(i).getString("id");
                            String name = friendsJSON.getJSONObject(i).getString("name");
                            String username = friendsJSON.getJSONObject(i)
                                    .getString("username");
                            String avatar = friendsJSON.getJSONObject(i)
                                    .getString("avatar");
                            mInviteFriends.add(new InviteFriendsAdapter.Friend(id, avatar,
                                    name, username));
                        }

                        mInviteFriends.endBatchedUpdates();
                    } else {
                        Util.Log("Unknown server error");
                        Toast.makeText(mActivity, "Unknown server error",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Util.Log("JSON error: " + e);
                    Toast.makeText(mActivity, "JSON error: " + e,
                            Toast.LENGTH_LONG).show();
                }
                Util.Log(response);

                mProgressBar.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Util.Log("Server error: " + error);
                Toast.makeText(mActivity, "Server error: " + error, Toast.LENGTH_LONG).show();

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
                params.put("user_id", id);
                return params;
            }
        };
        // Add the request to the RequestQueue.
        mQueue.add(stringRequest);
    }

}
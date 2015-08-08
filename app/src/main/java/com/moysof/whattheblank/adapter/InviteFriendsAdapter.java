package com.moysof.whattheblank.adapter;

import android.preference.PreferenceManager;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class InviteFriendsAdapter extends
        RecyclerView.Adapter<InviteFriendsAdapter.FriendHolder> {

    private FriendsAdapter mFriendsAdapter;
    private RequestQueue mQueue;
    private FriendsFragment mFragment;
    private MainActivity mActivity;
    private ImageLoader mImageLoader;
    private SortedList<Friend> friends;

    public InviteFriendsAdapter(MainActivity activity, FriendsFragment fragment, RequestQueue queue,
                                SortedList<Friend> friends, FriendsAdapter friendsAdapter) {
        this.friends = friends;
        mActivity = activity;
        mFragment = fragment;
        mQueue = queue;
        mFriendsAdapter = friendsAdapter;

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true).showImageOnFail(R.drawable.avatar_placeholder)
                .displayer(new FadeInBitmapDisplayer(250, true, false, false))
                .showImageOnLoading(android.R.color.white)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                activity).defaultDisplayImageOptions(defaultOptions).build();
        ImageLoader.getInstance().init(config);
        mImageLoader = ImageLoader.getInstance();
    }

    public static class Friend {

        public String userId;
        public String avatar;
        public String name;
        public String username;

        public Friend(String userId, String avatar, String name, String username) {
            this.userId = userId;
            this.avatar = avatar;
            this.name = name;
            this.username = username;
        }

        public String getUserId() {
            return userId;
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

    }

    public class FriendHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView avatarImg;
        public TextView nameTxt;
        public TextView usernameTxt;
        private ImageButton addBtn;

        public FriendHolder(View v) {
            super(v);
            avatarImg = (ImageView) v.findViewById(R.id.friends_img);
            nameTxt = (TextView) v.findViewById(R.id.friends_name_txt);
            usernameTxt = (TextView) v.findViewById(R.id.friends_username_txt);
            addBtn = (ImageButton) v.findViewById(R.id.friends_add_btn);

            if (addBtn != null) {
                addBtn.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            addClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FriendHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        return new FriendHolder(LayoutInflater.from(mActivity).inflate(
                R.layout.item_friends_pending, parent, false));
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(FriendHolder holder, int position) {
        Friend friend = friends.get(position);

        mImageLoader.displayImage(friend.getAvatar(), holder.avatarImg);
        holder.usernameTxt.setText("@" + friend.getUsername());
        holder.nameTxt.setText(friend.getName());
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    OnItemClickListener addClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(View v, int position) {
            addFriend(friends.get(position).getUserId());
        }
    };

    private void addFriend(final String friendId) {
        final String id = PreferenceManager.getDefaultSharedPreferences(mActivity)
                .getString("id", "");

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Util.URL_ADD_FRIEND, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseJSON = new JSONObject(response);
                    if (responseJSON.getString("result").equals("success")) {
                        mFriendsAdapter.loadInviteFriends();
                        mFragment.loadFriends();
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

                mFriendsAdapter.loadInviteFriends();
                mFragment.loadFriends();
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
                return params;
            }
        };
        // Add the request to the RequestQueue.
        mQueue.add(stringRequest);
    }

}
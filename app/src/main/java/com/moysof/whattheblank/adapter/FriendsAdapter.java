package com.moysof.whattheblank.adapter;

import android.preference.PreferenceManager;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.moysof.whattheblank.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

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
    public static final int ITEM_TYPE_FACEBOOK_HEADER = 4;
    public static final int ITEM_TYPE_FACEBOOK_FRIEND = 5;
    public static final int ITEM_TYPE_FACEBOOK_ADD_BUTTON = 6;
    private RequestQueue mQueue;
    private FriendsFragment mFragment;
    private MainActivity mActivity;
    private ImageLoader mImageLoader;
    private SortedList<Friend> friends;

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
    }

    public static class Friend {

        public String userId;
        public String avatar;
        public String name;
        public String username;
        public Integer type;

        public Friend(String userId, String avatar, String name, String username, Integer type) {
            this.userId = userId;
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
        private ImageButton addBtn;

        public FriendHolder(View v) {
            super(v);
            avatarImg = (ImageView) v.findViewById(R.id.friends_img);
            nameTxt = (TextView) v.findViewById(R.id.friends_name_txt);
            usernameTxt = (TextView) v.findViewById(R.id.friends_username_txt);
            facebookBtn = (Button) v.findViewById(R.id.friends_facebook_btn);
            addBtn = (ImageButton) v.findViewById(R.id.friends_add_btn);

            if (facebookBtn != null) {
                facebookBtn.setOnClickListener(this);
            } else if (addBtn != null) {
                addBtn.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.friends_facebook_btn) {
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
        } else if (getItemViewType(position) != ITEM_TYPE_FACEBOOK_ADD_BUTTON) {
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
            mActivity.inviteFriends();
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
                    VolleyError error
                            = new VolleyError(new String(volleyError.networkResponse.data));
                    volleyError = error;
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

        mFragment.loadFacebookFriends();
    }

}
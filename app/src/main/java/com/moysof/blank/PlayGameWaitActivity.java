package com.moysof.blank;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.moysof.blank.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class PlayGameWaitActivity extends AppCompatActivity {

    private ImageLoader mImageLoader;
    private ImageView mImg;
    public static String EXTRA_HOST_NAME = "host_name";
    public static String EXTRA_HOST_USERNAME = "host_username";
    public static String EXTRA_HOST_AVATAR = "host_avatar";
    public static String EXTRA_IS_HOST = "is_host";
    public static String EXTRA_GAME_ID = "game_id";
    public static String EXTRA_PLAYER_ID = "player_id";
    private boolean mIsHost;
    private String mGameId;
    private String mPlayerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game_wait);

        mImg = (ImageView) findViewById(R.id.play_wait_img);
        ((TextView) findViewById(R.id.play_name_txt)).setText(getIntent()
                .getStringExtra(EXTRA_HOST_NAME));
        ((TextView) findViewById(R.id.play_username_txt)).setText(getIntent()
                .getStringExtra(EXTRA_HOST_USERNAME));

        mIsHost = getIntent().getBooleanExtra(EXTRA_IS_HOST, false);
        mGameId = getIntent().getStringExtra(EXTRA_GAME_ID);
        mPlayerId = getIntent().getStringExtra(EXTRA_PLAYER_ID);

        initAvatar();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Util.BROADCAST_PLAY_AGAIN);
        registerReceiver(mReceiver, filter);
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            PlayGameWaitActivity.this.setResult(PlayWonFragment.RESULT_PLAY_AGAIN);
            finish();
        }
    };

    private void initAvatar() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(250, true, false, false))
                .showImageOnLoading(android.R.color.white)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this).defaultDisplayImageOptions(defaultOptions).build();
        ImageLoader.getInstance().init(config);
        mImageLoader = ImageLoader.getInstance();

        mImageLoader.displayImage(getIntent().getStringExtra(EXTRA_HOST_AVATAR), mImg);
    }


    @Override
    public void onBackPressed() {
        showExitDialog();
    }

    private void showExitDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this,
                R.style.MaterialDialogStyle);
        dialogBuilder.setTitle("Are you sure?");
        dialogBuilder.setMessage("You'll loose all game progress, if you leave now.");

        dialogBuilder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                closeGame();
                dialog.cancel();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialogBuilder.create().show();
    }

    private void closeGame() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        final ProgressDialog progressDialog;
        String url;
        if (!mIsHost) {
            progressDialog = ProgressDialog.show(this, "",
                    "Leaving game...");
            url = Util.URL_LEAVE_GAME;
        } else {
            progressDialog = ProgressDialog.show(this, "",
                    "Closing game...");
            url = Util.URL_CLOSE_GAME;
        }

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.cancel();
                Util.Log(response);
                try {
                    JSONObject responseJSON = new JSONObject(response);
                    if (responseJSON.getString("result").equals("success")) {
                        StartGameActivity.sActivity.finish();
                        finish();
                    } else if (responseJSON.getString("result").equals("empty")) {
                        Toast.makeText(PlayGameWaitActivity.this, "Some fields are empty",
                                Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(PlayGameWaitActivity.this, "Unknown server error",
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                } catch (JSONException e) {
                    if (Util.isDebugging()) {
                        Toast.makeText(PlayGameWaitActivity.this, "JSON error: " + response,
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(PlayGameWaitActivity.this, "Unknown server error",
                                Toast.LENGTH_LONG).show();
                    }
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(PlayGameWaitActivity.this, "Server error",
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
                params.put("game_id", mGameId);
                if (!mIsHost) {
                    params.put("player_id", mPlayerId);
                }
                return params;
            }


        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}

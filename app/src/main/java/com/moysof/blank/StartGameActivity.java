package com.moysof.blank;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.moysof.blank.util.Util;
import com.moysof.blank.view.ControllableViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class StartGameActivity extends AppCompatActivity {

    public static ControllableViewPager sViewPager;
    public static SectionsPagerAdapter sAdapter;
    public static String EXTRA_GAME_ID = "game_id";
    public static String EXTRA_PLAYER_ID = "player_id";
    public static String EXTRA_IS_HOST = "is_host";
    public static String EXTRA_PENDING_PLAYERS = "pending_players";
    public static String EXTRA_PENDING_COUNT = "pending_count";
    public static String EXTRA_NUMBER_CARDS = "number_cards";
    private String mGameId;
    private String mPlayerId;
    private boolean mIsHost;
    public static JSONArray sPendingPlayers;
    private int sPendingCount;
    public static int sCurrentPlayerNum = 0;
    private StartWaitingFragment mWaitingFragment;
    public static StartGameActivity sActivity;
    private int mNumberCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        sActivity = this;

        mGameId = getIntent().getStringExtra(EXTRA_GAME_ID);
        mPlayerId = getIntent().getStringExtra(EXTRA_PLAYER_ID);
        mIsHost = getIntent().getBooleanExtra(EXTRA_IS_HOST, false);
        sPendingCount = getIntent().getIntExtra(EXTRA_PENDING_COUNT, 0);
        mNumberCards = getIntent().getIntExtra(EXTRA_NUMBER_CARDS, 0);

        try {
            sPendingPlayers = new JSONArray(getIntent().getStringExtra(EXTRA_PENDING_PLAYERS));
        } catch (Exception e) {
            sPendingPlayers = null;
        }

        sViewPager = (ControllableViewPager) findViewById(R.id.start_view_pager);

        sCurrentPlayerNum = 0;

        sAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        sViewPager.setAdapter(sAdapter);

        // Add a receiver to listen for closing the game by host
        IntentFilter filter = new IntentFilter();
        filter.addAction(Util.BROADCAST_CLOSED_GAME);
        if (mIsHost) {
            filter.addAction(Util.BROADCAST_BEGIN_GAME);
        }
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == PlayWonFragment.RESULT_PLAY_AGAIN) {
            sCurrentPlayerNum = 0;
            sViewPager.setAdapter(sAdapter);
        }
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Util.BROADCAST_CLOSED_GAME)) {
                // Host has left the game
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(StartGameActivity.this,
                        R.style.MaterialDialogStyle);
                dialogBuilder.setTitle("Error");
                dialogBuilder.setMessage("Host has left the game.");
                dialogBuilder.setCancelable(false);
                dialogBuilder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                });
                dialogBuilder.create().show();
            } else if (intent.getAction().equals(Util.BROADCAST_BEGIN_GAME)) {
                mWaitingFragment.openPlayScreen();
            }
        }
    };

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Util.Log("start cards = " + position + " == " + (mNumberCards + 1));
            // getItem is called to instantiate the fragment for the given page.
            if (position == 0) {
                return StartReadyFragment.newInstance(mNumberCards);
            } else if (position == mNumberCards + 1) {
                mWaitingFragment = StartWaitingFragment.newInstance(mGameId, mPlayerId, mIsHost);
                return mWaitingFragment;
            } else {
                String name = "";
                String username = "";
                String avatar = "";
                try {
                    name = sPendingPlayers.getJSONObject(sCurrentPlayerNum).getString("name");
                    if (sPendingPlayers.getJSONObject(sCurrentPlayerNum).has("username")) {
                        username = sPendingPlayers.getJSONObject(sCurrentPlayerNum)
                                .getString("username");
                        avatar = sPendingPlayers.getJSONObject(sCurrentPlayerNum)
                                .getString("avatar");
                    }
                } catch (Exception e) {
                    Util.Log("Can't get player info");
                }
                return StartCreateFragment.newInstance(position, mGameId, mPlayerId,
                        mIsHost, name, username, avatar, sPendingCount, mNumberCards);
            }
        }

        @Override
        public int getCount() {
            return mNumberCards + 2;
        }

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
                        finish();
                    } else if (responseJSON.getString("result").equals("empty")) {
                        Toast.makeText(StartGameActivity.this, "Some fields are empty",
                                Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(StartGameActivity.this, "Unknown server error",
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                } catch (JSONException e) {
                    if (Util.isDebugging()) {
                        Toast.makeText(StartGameActivity.this, "JSON error: " + response,
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(StartGameActivity.this, "Unknown server error",
                                Toast.LENGTH_LONG).show();
                    }
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(StartGameActivity.this, "Server error",
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

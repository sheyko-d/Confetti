package com.moysof.whattheblank;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.moysof.whattheblank.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class HostLobbyActivity extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    public static final String EXTRA_ID = "game_id";
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_PASSWORD = "password";
    public static final String EXTRA_NUMBER_TEAMS = "number_teams";
    public static final String EXTRA_NUMBER_PLAYERS = "number_players";
    public static final String EXTRA_NUMBER_CARDS = "number_cards";
    public static final String EXTRA_NUMBER_TIME = "number_time";
    public static final String EXTRA_PLAYER_ID = "player_id";
    private String mGameId;
    private String mPassword;
    private Integer mNumberTeams;
    private Integer mNumberPlayers;
    private Integer mNumberCards;
    private Integer mNumberTime;
    private RequestQueue mQueue;
    private HostLobbyPlayersFragment mPlayersFragment;
    private String mPlayerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        mGameId = getIntent().getStringExtra(EXTRA_ID);
        mPassword = getIntent().getStringExtra(EXTRA_PASSWORD);
        mNumberTeams = getIntent().getIntExtra(EXTRA_NUMBER_TEAMS, 0);
        mNumberPlayers = getIntent().getIntExtra(EXTRA_NUMBER_PLAYERS, 0);
        mNumberCards = getIntent().getIntExtra(EXTRA_NUMBER_CARDS, 0);
        mNumberTime = getIntent().getIntExtra(EXTRA_NUMBER_TIME, 0);
        mPlayerId = getIntent().getStringExtra(EXTRA_PLAYER_ID);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        initTabs();

        findViewById(R.id.join_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitDialog();
            }
        });

        mQueue = Volley.newRequestQueue(this);

        // Add a receiver to listen for incoming messages
        IntentFilter filter = new IntentFilter();
        filter.addAction(Util.BROADCAST_JOINED_GAME);
        registerReceiver(mJoinedReceiver, filter);
    }

    BroadcastReceiver mJoinedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mViewPager.getCurrentItem()==0) {
                HostLobbyGameFragment.getTeams();
                mPlayersFragment.getPlayers();
            } else {
                mPlayersFragment.getPlayers();
                HostLobbyGameFragment.getTeams();
            }
        }
    };

    private void initTabs() {
        ((TextView) findViewById(R.id.join_title_txt)).setText(Html.fromHtml("<b>" + getIntent()
                .getStringExtra(EXTRA_NAME) + "</b>"));

        Typeface droidSerifMonoTF = Typeface.createFromAsset(getAssets(),
                "fonts/BasicTitleFont.ttf");
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            TextView t = new TextView(this);
            t.setText(Html.fromHtml("<b>" + mSectionsPagerAdapter.getPageTitle(i) + "</b>"));
            t.setTypeface(droidSerifMonoTF);
            t.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            t.setTextColor(getResources().getColor(R.color.primary));

            mTabLayout.addTab(mTabLayout.newTab().setCustomView(t));
        }

        // Add tabs listener
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            if (position == 0) {
                mPlayersFragment = HostLobbyPlayersFragment.newInstance(mGameId, mNumberTeams,
                        mNumberPlayers, mQueue, mPlayerId);

                return HostLobbyGameFragment.newInstance(mGameId, mPassword, mNumberTeams,
                        mNumberPlayers, mNumberCards, mNumberTime, mQueue, mPlayerId,
                        mPlayersFragment);
            } else {
                return mPlayersFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public String getPageTitle(int position) {
            if (position == 0) {
                return getString(R.string.host_lobby_tab_game);
            } else {
                return getString(R.string.host_lobby_tab_players);
            }
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
        dialogBuilder.setMessage("You'll loose all game info, if you leave now.");

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

        final ProgressDialog progressDialog = ProgressDialog.show(this, "",
                "Closing game...");

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Util.URL_CLOSE_GAME, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.cancel();
                Util.Log(response);
                try {
                    JSONObject responseJSON = new JSONObject(response);
                    if (responseJSON.getString("result").equals("success")) {
                        finish();
                    } else if (responseJSON.getString("result").equals("empty")) {
                        Toast.makeText(HostLobbyActivity.this, "Some fields are empty",
                                Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(HostLobbyActivity.this, "Unknown server error",
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                } catch (JSONException e) {
                    if (Util.isDebugging()) {
                        Toast.makeText(HostLobbyActivity.this, "JSON error: " + response,
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(HostLobbyActivity.this, "Unknown server error",
                                Toast.LENGTH_LONG).show();
                    }
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(HostLobbyActivity.this, "Server error",
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
                return params;
            }


        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

}

package com.moysof.whattheblank;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.moysof.whattheblank.util.Util;
import com.moysof.whattheblank.view.ControllableViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class PlayGameActivity extends AppCompatActivity {

    public static ControllableViewPager sViewPager;
    public static SectionsPagerAdapter sAdapter;
    public static final String EXTRA_GAME_ID = "game_id";
    public static final String EXTRA_CARDS = "cards";
    public static final String EXTRA_TEAMS = "teams";
    public static final String EXTRA_TIME = "time";
    public static boolean sDontStopTimer = false;
    private String mGameId;
    public static ArrayList<Card> sCards = new ArrayList<>();
    public static ArrayList<Card> sSolvedCards = new ArrayList<>();
    private Integer mTime;
    private Integer mUsedTime = 0;
    private Handler mHandler;
    private Runnable mTimerRunnable;
    private PlayGameActivity mActivity;
    public static ArrayList<Team> sTeams = new ArrayList<>();
    private int mCurrentTeamNum = 0;
    private int mCurrentRoundNum = 0;
    private boolean mTimerIsRunning = false;
    public static String sTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);

        mActivity = this;

        mGameId = getIntent().getStringExtra(EXTRA_GAME_ID);
        mTime = getIntent().getIntExtra(EXTRA_TIME, 0);

        sTeams.clear();
        try {
            JSONArray teams = new JSONArray(getIntent().getStringExtra(EXTRA_TEAMS));
            int teamsCount = teams.length();
            for (int i = 0; i < teamsCount; i++) {
                String id = teams.getJSONObject(i).getString("id");
                String colorHex = teams.getJSONObject(i).getString("color");
                sTeams.add(new Team(id, colorHex, i + 1));
            }
        } catch (Exception e) {
            Util.Log("Can't get teams");
        }

        mHandler = new Handler();
        mTimerRunnable = new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mTimerIsRunning) {
                            // Check if timer has run out
                            if (mUsedTime <= mTime) {
                                sendBroadcast(new Intent(Util.BROADCAST_TIMER_TICK)
                                        .putExtra(PlayCardFragment.EXTRA_TIME, mTime - mUsedTime));
                                mUsedTime++;
                                mHandler.postDelayed(this, 1000);
                            } else {
                                switchToNextTeam();

                                // Open beginning screen for the next team
                                sViewPager.setAdapter(sAdapter);

                                // Vibrate for half a second
                                ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE))
                                        .vibrate(500);
                            }
                        }
                    }
                });
            }
        };

        sViewPager = (ControllableViewPager) findViewById(R.id.play_view_pager);

        sAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        updateCards();

        // Set up the ViewPager with the sections adapter.
        sViewPager.setAdapter(sAdapter);
    }

    private void updateCards() {
        sCards.clear();
        try {
            JSONArray cards = new JSONArray(getIntent().getStringExtra(EXTRA_CARDS));
            int cardsCount = cards.length();
            for (int i = 0; i < cardsCount; i++) {
                String id = cards.getJSONObject(i).getString("id");
                String playerId = cards.getJSONObject(i).getString("player_id");
                String img = cards.getJSONObject(i).getString("img");
                sCards.add(new Card(id, playerId, img));
            }
        } catch (Exception e) {
            Util.Log("Can't get cards");
        }
        sAdapter.notifyDataSetChanged();
    }

    public void switchToNextTeam() {
        if (mCurrentTeamNum == sTeams.size() - 1) {
            mCurrentTeamNum = 0;
        } else {
            mCurrentTeamNum++;
        }
    }

    public void switchToFirstTeam() {
        mCurrentTeamNum = 0;
    }

    public void switchToNextRound() {
        updateCards();
        shuffleCards();
        mCurrentRoundNum++;
    }

    private void shuffleCards() {
        long seed = System.nanoTime();
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < sCards.size(); i++) {
            cards.add(sCards.get(i));
        }

        sCards.clear();
        Collections.shuffle(cards, new Random(seed));

        for (int i = 0; i < cards.size(); i++) {
            sCards.add(cards.get(i));
        }
        sAdapter.notifyDataSetChanged();
    }

    public void startTimer() {
        mUsedTime = 0;
        mTimerIsRunning = true;
        mTimerRunnable.run();
    }

    public void stopTimer() {
        if (!sDontStopTimer) {
            mUsedTime = 0;
            mTimerIsRunning = false;
            if (mHandler != null) {
                mHandler.removeCallbacksAndMessages(mTimerRunnable);
            }
        }
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            if (position == 0) {
                return PlayWaitingFragment.newInstance(mActivity, mCurrentTeamNum, mTime);
            } else if (position == sCards.size() + 1) {
                return PlayWonFragment.newInstance(mActivity, mGameId);
            } else {
                return PlayCardFragment.newInstance(getCount(), sCards.get(position - 1),
                        mCurrentTeamNum, mActivity, mCurrentRoundNum);
            }
        }

        @Override
        public int getCount() {
            return sCards.size() + 2;
        }

    }

    public static class Card {

        public String id;
        public String playerId;
        public String img;
        public Integer solvedTime = -1;

        public Card(String id, String playerId, String img) {
            this.id = id;
            this.playerId = playerId;
            this.img = img;
        }

        public String getId() {
            return id;
        }

        public String getPlayerId() {
            return playerId;
        }

        public String getImage() {
            return img;
        }

        public void setSolvedTime(int sec) {
            solvedTime = sec;
        }

        public Integer getSolvedTime() {
            return solvedTime;
        }

    }

    public static class Team {

        public String id;
        public String colorHex;
        public Integer score = 0;
        public Integer number;

        public Team(String id, String colorHex, Integer number) {
            this.id = id;
            this.colorHex = colorHex;
            this.number = number;
        }

        public String getId() {
            return id;
        }

        public String getColorHex() {
            return colorHex;
        }

        public Integer getColor() {
            return Color.parseColor("#" + colorHex);
        }

        public Integer getScore() {
            return score;
        }

        public Integer getNumber() {
            return number;
        }

        public void addPoint() {
            score++;
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
                    } else if (responseJSON.getString("result").equals("empty")) {
                        Toast.makeText(PlayGameActivity.this, "Some fields are empty",
                                Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(PlayGameActivity.this, "Unknown server error",
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                } catch (JSONException e) {
                    if (Util.isDebugging()) {
                        Toast.makeText(PlayGameActivity.this, "JSON error: " + response,
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(PlayGameActivity.this, "Unknown server error",
                                Toast.LENGTH_LONG).show();
                    }
                }
                finish();
                StartGameActivity.sActivity.finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(PlayGameActivity.this, "Server error",
                        Toast.LENGTH_LONG).show();
                Util.Log("Server error: " + error);
                finish();
                StartGameActivity.sActivity.finish();
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

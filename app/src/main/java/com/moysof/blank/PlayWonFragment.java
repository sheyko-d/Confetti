package com.moysof.blank;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.moysof.blank.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlayWonFragment extends Fragment {

    private static PlayGameActivity sActivity;
    private static String sGameId;
    private ViewGroup mOrderLayout;
    private TextView mDescTxt;
    public final static int RESULT_PLAY_AGAIN = 0;

    public static PlayWonFragment newInstance(PlayGameActivity activity, String gameId) {
        sActivity = activity;
        sGameId = gameId;

        return new PlayWonFragment();
    }

    public PlayWonFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_play_won, container, false);

        mOrderLayout = (ViewGroup) rootView.findViewById(R.id.play_won_order_layout);
        mDescTxt = (TextView) rootView.findViewById(R.id.play_won_desc_txt);

        int teamsCount = PlayGameActivity.sTeams.size();
        int maxScore = -1;
        for (int i = 0; i < teamsCount; i++) {
            int score = PlayGameActivity.sTeams.get(i).getScore();
            addTeamCircle(PlayGameActivity.sTeams.get(i).getColor(),
                    score);

            if (score > maxScore) {
                maxScore = score;
            }
        }

        ArrayList<PlayGameActivity.Team> winnerTeams = new ArrayList<>();
        for (int i = 0; i < teamsCount; i++) {
            if (PlayGameActivity.sTeams.get(i).getScore() == maxScore) {
                winnerTeams.add(PlayGameActivity.sTeams.get(i));
            }
        }

        String desc;
        StringBuilder teamTxtBuilder = new StringBuilder();
        for (int i = 0; i < winnerTeams.size(); i++) {
            teamTxtBuilder.append("<font color='#" + winnerTeams.get(i).getColorHex()
                    + "'>TEAM " + winnerTeams.get(i).getNumber() + "</font>");
            if (i < winnerTeams.size() - 2) {
                teamTxtBuilder.append(", ");
            } else if (i == winnerTeams.size() - 2) {
                teamTxtBuilder.append(" and ");
            }
        }
        if (winnerTeams.size() == 1) {
            desc = String.format(getResources().getString(R.string.play_title_won),
                    teamTxtBuilder.toString());
        } else {
            desc = String.format(getResources().getString(R.string.play_title_won_draw),
                    teamTxtBuilder.toString());
        }

        mDescTxt.setText(Html.fromHtml(desc), TextView.BufferType.SPANNABLE);

        rootView.findViewById(R.id.play_again_btn).setOnClickListener(mButtonClickListener);
        rootView.findViewById(R.id.play_quit_btn).setOnClickListener(mButtonClickListener);

        return rootView;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            PlayGameActivity.sViewPager.setSwipeEnabled(false);
            sActivity.stopTimer();

            updateCardsTime();
        }
    }

    private void updateCardsTime() {
        final JSONArray cards = new JSONArray();
        for (int i = 0; i < PlayGameActivity.sSolvedCards.size(); i++) {
            try {
                JSONObject card = new JSONObject();
                card.put("id", PlayGameActivity.sSolvedCards.get(i).getId());
                card.put("player_id", PlayGameActivity.sSolvedCards.get(i).getPlayerId());
                card.put("time", PlayGameActivity.sSolvedCards.get(i).getSolvedTime());
                cards.put(card);
            } catch (Exception e) {
                Util.Log("Can't get solved card = " + e);
            }
        }

        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "",
                "Loading...");

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Util.URL_UPDATE_CARDS_TIME, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.cancel();
                Util.Log(response);
                try {
                    JSONObject responseJSON = new JSONObject(response);
                    if (responseJSON.getString("result").equals("success")) {
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
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(getActivity(), "Server error",
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
                params.put("cards", cards.toString());
                params.put("game_id", sGameId);
                return params;
            }

        };
        // Add the request to the RequestQueue.
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    private void playAgain() {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "",
                "Loading...");

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Util.URL_PLAY_AGAIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.cancel();
                Util.Log(response);
                try {
                    JSONObject responseJSON = new JSONObject(response);
                    if (responseJSON.getString("result").equals("success")) {
                        getActivity().setResult(RESULT_PLAY_AGAIN);
                        getActivity().finish();
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
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(getActivity(), "Server error",
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
                params.put("game_id", sGameId);
                return params;
            }


        };
        // Add the request to the RequestQueue.
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    View.OnClickListener mButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.play_again_btn) {
                playAgain();
            } else {
                quit();
            }
        }
    };

    private void quit() {
        getActivity().finish();
        StartGameActivity.sActivity.finish();
    }

    private void addTeamCircle(int color, int score) {
        View teamView = LayoutInflater.from(getActivity())
                .inflate(R.layout.item_play_team, null);
        // Set height to 36 dp + padding (16 dp vertical or 8 dp horizontal)
        teamView.setLayoutParams(new ViewGroup.LayoutParams(Util.convertDpToPixel(36 + 8),
                Util.convertDpToPixel(36 + 16)));
        ((ImageView) teamView.findViewById(R.id.play_waiting_team_img))
                .setImageDrawable(new ColorDrawable(color));
        ((TextView) teamView.findViewById(R.id.play_waiting_team_score_txt))
                .setText(Html.fromHtml("<b>" + score + "</b>"));
        mOrderLayout.addView(teamView);
    }

}
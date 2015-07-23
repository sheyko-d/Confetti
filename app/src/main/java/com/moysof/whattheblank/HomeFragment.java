package com.moysof.whattheblank;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private Button mJoinBtn;
    private Button mHostBtn;
    private Button mHowToBtn;
    private TextView mStatGamesTotal;
    private TextView mStatTimeSolved;
    private TextView mStatRanking;
    private SharedPreferences mPrefs;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        mJoinBtn = (Button) rootView.findViewById(R.id.home_join_btn);
        mHostBtn = (Button) rootView.findViewById(R.id.home_host_btn);
        mHowToBtn = (Button) rootView.findViewById(R.id.home_button_how_to);

        mStatGamesTotal = (TextView) rootView.findViewById(R.id.home_stat_games_total);
        mStatTimeSolved = (TextView) rootView.findViewById(R.id.home_stat_time_solved);
        mStatRanking = (TextView) rootView.findViewById(R.id.home_stat_ranking);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        mJoinBtn.setOnClickListener(mGameClickListener);
        mHostBtn.setOnClickListener(mGameClickListener);
        mHowToBtn.setOnClickListener(mGameClickListener);

        initStats();

        return rootView;
    }

    private void initStats() {
        mStatGamesTotal.setText(mPrefs.getString("games_played", "0"));
        mStatTimeSolved.setText(mPrefs.getString("time_solved", "0") + " sec");
        mStatRanking.setText("top " + mPrefs.getString("ranking", "0") + "%");

        loadStats();
    }

    private void loadStats() {
        final String id = mPrefs.getString("id", "");

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Util.URL_GET_STATS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseJSON = new JSONObject(response);
                            if (responseJSON.getString("result").equals("success")) {
                                String gamesPlayed = responseJSON.getString("games_played");
                                String timeSolved = responseJSON.getString("time_solved");
                                String ranking = responseJSON.getString("ranking");

                                mPrefs.edit().putString("games_played", gamesPlayed)
                                        .putString("time_solved", timeSolved)
                                        .putString("ranking", ranking).apply();

                                mStatGamesTotal.setText(gamesPlayed);
                                mStatTimeSolved.setText(timeSolved + " sec");
                                mStatRanking.setText("top " + ranking + "%");
                            } else {
                                Util.Log("Unknown server error");
                            }
                        } catch (JSONException e) {
                            Util.Log("JSON error: " + response);
                        }
                        Util.Log(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Util.Log("Server error: " + error);
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
                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    View.OnClickListener mGameClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.home_host_btn) {
                hostGame();
            } else if (v.getId() == R.id.home_join_btn) {
                joinGame();
            } else {
                showHowTo();
            }
        }
    };

    private void showHowTo() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(),
                R.style.MaterialDialogStyle);
        dialogBuilder.setTitle("How to Play");
        dialogBuilder.setMessage(getActivity().getString(R.string.home_how_to_desc));
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialogBuilder.create().show();
    }

    public void joinGame() {
        startActivity(new Intent(getActivity(), JoinActivity.class));
    }

    public void hostGame() {
        startActivity(new Intent(getActivity(), HostActivity.class));
    }
}
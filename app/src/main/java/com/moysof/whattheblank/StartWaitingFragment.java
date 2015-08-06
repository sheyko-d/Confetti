package com.moysof.whattheblank;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.moysof.whattheblank.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class StartWaitingFragment extends Fragment {

    private static String sGameId;
    private static String sPlayerId;
    private static boolean sIsHost;
    public static ViewGroup sOrderLayout;
    private Button mWaitingBtn;

    public static StartWaitingFragment newInstance(String gameId, String playerId, Boolean isHost) {
        sGameId = gameId;
        sPlayerId = playerId;
        sIsHost = isHost;
        return new StartWaitingFragment();
    }

    public StartWaitingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_start_waiting, container, false);

        sOrderLayout = (ViewGroup) rootView.findViewById(R.id.start_waiting_order_layout);

        StartGameActivity.sViewPager.setSwipeEnabled(false);

        mWaitingBtn = (Button) rootView.findViewById(R.id.start_waiting_btn);
        mWaitingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginGame();
            }
        });

        return rootView;
    }



    private void beginGame() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Util.URL_BEGIN_GAME, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Util.Log(response);
                try {
                    JSONObject responseJSON = new JSONObject(response);
                    if (responseJSON.getString("result").equals("success")) {
                        if (sIsHost) {
                            mWaitingBtn.setEnabled(false);
                            mWaitingBtn.setText("WAITING FOR PLAYERS...");
                        } else {
                            String hostName = responseJSON.getString("host_name");
                            String hostUsername = responseJSON.getString("host_username");
                            String hostAvatar = responseJSON.getString("host_avatar");

                            startActivity(new Intent(getActivity(), PlayGameWaitActivity.class)
                                    .putExtra(PlayGameWaitActivity.EXTRA_HOST_NAME, hostName)
                                    .putExtra(PlayGameWaitActivity.EXTRA_HOST_USERNAME,
                                            hostUsername)
                                    .putExtra(PlayGameWaitActivity.EXTRA_HOST_AVATAR, hostAvatar)
                                    .putExtra(PlayGameWaitActivity.EXTRA_GAME_ID, sGameId)
                                    .putExtra(PlayGameWaitActivity.EXTRA_IS_HOST, sIsHost)
                                    .putExtra(PlayGameWaitActivity.EXTRA_PLAYER_ID, sPlayerId));
                            getActivity().finish();
                        }
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

                progressDialog.cancel();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Server error",
                        Toast.LENGTH_LONG).show();
                Util.Log("Server error: " + error);
                progressDialog.cancel();
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
                params.put("player_id", sPlayerId);
                params.put("game_id", sGameId);
                return params;
            }


        };
        // Add the request to the RequestQueue.
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    public void openPlayScreen() {
        startActivity(new Intent(getActivity(), PlayGameActivity.class));
        getActivity().finish();
    }

    public static void addTeamCircle(int color) {
        CircleImageView teamImg = (CircleImageView) LayoutInflater.from(StartGameActivity.sActivity)
                .inflate(R.layout.img_start_team, null);
        teamImg.setImageDrawable(new ColorDrawable(color));
        teamImg.setLayoutParams(new ViewGroup.LayoutParams(Util.convertDpToPixel(36),
                Util.convertDpToPixel(36)));
        sOrderLayout.addView(teamImg);
    }

    public static void addTeamDivider() {
        View teamDivider = LayoutInflater.from(StartGameActivity.sActivity)
                .inflate(R.layout.divider_start_team, null);
        teamDivider.setLayoutParams(new ViewGroup.LayoutParams(Util.convertDpToPixel(36),
                Util.convertDpToPixel(36)));
        sOrderLayout.addView(teamDivider);
    }

}
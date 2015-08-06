package com.moysof.whattheblank.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.util.SortedList;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.moysof.whattheblank.HostLobbyGameFragment;
import com.moysof.whattheblank.R;
import com.moysof.whattheblank.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HostTeamsAdapter extends
        RecyclerView.Adapter<HostTeamsAdapter.TeamsHolder> {

    private HostLobbyGameFragment mGameFragment;
    private String mGameId;
    private Integer mNumberPlayers;
    private Context mContext;
    private SortedList<Team> teams;

    public HostTeamsAdapter(Context context, SortedList<Team> teams, Integer numberPlayers,
                            String gameId, HostLobbyGameFragment gameFragment) {
        mContext = context;
        mNumberPlayers = numberPlayers;
        this.teams = teams;
        mGameId = gameId;
        mGameFragment = gameFragment;
    }

    public static class Team {

        public String id;
        public Integer number;
        public Integer assignedCount;
        public String colorHex;

        public Team(String id, Integer number, Integer assignedCount, String colorHex) {
            this.id = id;
            this.number = number;
            this.assignedCount = assignedCount;
            this.colorHex = colorHex;
        }

        public String getId() {
            return id;
        }

        public Integer getNumber() {
            return number;
        }

        public Integer getAssignedCount() {
            return assignedCount;
        }

        public Integer getColor() {
            return Color.parseColor("#" + colorHex);
        }

        public String getColorHex() {
            return colorHex;
        }

    }

    public class TeamsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView titleTxt;
        public TextView countTxt;
        public View colorView;

        public TeamsHolder(View v) {
            super(v);
            titleTxt = (TextView) v.findViewById(R.id.host_team_title_txt);
            countTxt = (TextView) v.findViewById(R.id.host_team_count_txt);
            colorView = v.findViewById(R.id.host_team_color_view);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            pickColorClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TeamsHolder onCreateViewHolder(ViewGroup parent,
                                          int viewType) {
        return new TeamsHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_host_team, parent, false));
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(TeamsHolder holder, int position) {
        Team team = teams.get(position);

        holder.titleTxt.setText("Team " + (team.getNumber() + 1));
        if (team.getAssignedCount() > 0) {
            holder.countTxt.setText(team.getAssignedCount() + " of "
                    + mNumberPlayers + " players assigned");
        } else {
            holder.countTxt.setText("No players assigned");
        }
        holder.colorView.setBackgroundColor(team.getColor());
    }

    @Override
    public int getItemCount() {
        return teams.size();
    }

    private AlertDialog mDialog;
    private View mProgressBar;
    private RecyclerView mRecyclerView;
    
    OnItemClickListener pickColorClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(View v, final int position) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext,
                    R.style.MaterialDialogStyle);
            dialogBuilder.setTitle("Pick Team Color");

            View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_team_color,
                    null);
            mRecyclerView = (RecyclerView) dialogView.findViewById
                    (R.id.colors_recycler_view);
            mProgressBar = dialogView.findViewById(R.id.colors_progress_bar);
            GridLayoutManager layoutManager = new GridLayoutManager(mContext, 4);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setHasFixedSize(true);

            int teamsCount = teams.size();
            ArrayList<String> otherTeamColors = new ArrayList<>();
            for (int i = 0; i < teamsCount; i++) {
                if (i != position) {
                    otherTeamColors.add(teams.get(i).getColorHex());
                }
            }

            final ColorsAdapter adapter = new ColorsAdapter(mContext, otherTeamColors);
            adapter.setTeamColor(teams.get(position).getColorHex());
            mRecyclerView.setAdapter(adapter);

            dialogBuilder.setView(dialogView);
            dialogBuilder.setPositiveButton("OK", null);
            dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            mDialog = dialogBuilder.create();
            mDialog.show();
            mDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            changeTeamColor(mGameId, teams.get(position).getId(),
                                    adapter.getTeamColor());
                        }
                    });
        }
    };

    private void changeTeamColor(final String gameId, final String teamId, final String color) {
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Util.URL_CHANGE_TEAM_COLOR, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseJSON = new JSONObject(response);
                    if (responseJSON.getString("result").equals("success")) {
                        mDialog.cancel();
                        mGameFragment.getTeams();
                    } else if (responseJSON.getString("result").equals("empty")) {
                        Toast.makeText(mContext, "Some fields are empty",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mContext, "Unknown server error",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    if (Util.isDebugging()) {
                        Toast.makeText(mContext, "JSON error: " + response,
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mContext, "Unknown server error",
                                Toast.LENGTH_LONG).show();
                    }
                }

                Util.Log(response);
                mProgressBar.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, "Server error",
                        Toast.LENGTH_LONG).show();
                Util.Log("Server error: " + error);

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
                params.put("team_id", teamId);
                params.put("game_id", gameId);
                params.put("color", color);
                return params;
            }


        };
        // Add the request to the RequestQueue.
        Volley.newRequestQueue(mContext).add(stringRequest);
    }
}
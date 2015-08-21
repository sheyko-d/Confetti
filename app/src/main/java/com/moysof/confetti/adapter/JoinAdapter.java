package com.moysof.confetti.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.moysof.confetti.JoinLobbyActivity;
import com.moysof.confetti.R;
import com.moysof.confetti.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JoinAdapter extends
        RecyclerView.Adapter<JoinAdapter.GameHolder> {

    private Context mContext;
    private SortedList<Game> games;
    public static final int ITEM_TYPE_GAME = 0;
    public static final int ITEM_TYPE_GAME_FRIEND = 1;
    private AlertDialog mDialog;

    public JoinAdapter(Context context, SortedList<Game> games) {
        mContext = context;
        this.games = games;
    }

    public static class Game {

        public Integer type;
        public String gameId;
        public String name;
        public Integer password;
        public String username;
        public Integer numberTeams;
        public Integer numberPlayers;
        public Integer numberCards;
        public Integer time;
        public Integer assignedNumber;
        public String timestamp;

        public Game(Integer type, String gameId, String name, Integer password, String username,
                    Integer numberTeams, Integer numberPlayers, Integer numberCards, Integer time,
                    Integer assignedNumber, String timestamp) {
            this.type = type;
            this.gameId = gameId;
            this.name = name;
            this.password = password;
            this.username = username;
            this.numberTeams = numberTeams;
            this.numberPlayers = numberPlayers;
            this.numberCards = numberCards;
            this.time = time;
            this.assignedNumber = assignedNumber;
            this.timestamp = timestamp;
        }

        public Integer getType() {
            return type;
        }

        public String getGameId() {
            return gameId;
        }

        public String getName() {
            return name;
        }

        public Integer getPassword() {
            return password;
        }

        public String getUsername() {
            return username;
        }

        public Integer getTeamsNumber() {
            return numberTeams;
        }

        public Integer getPlayersNumber() {
            return numberPlayers;
        }

        public Integer getCardsNumber() {
            return numberCards;
        }

        public Integer getTime() {
            return time;
        }

        public Integer getAssignedNumber() {
            return assignedNumber;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }

    public class GameHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView nameTxt;
        public TextView usernameTxt;
        public Button joinBtn;

        public GameHolder(View v) {
            super(v);
            nameTxt = (TextView) v.findViewById(R.id.join_game_name_txt);
            usernameTxt = (TextView) v.findViewById(R.id.join_game_username_txt);
            joinBtn = (Button) v.findViewById(R.id.join_game_btn);

            joinBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            joinClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return games.get(position).getType();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public GameHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        if (viewType == ITEM_TYPE_GAME) {
            return new GameHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_games, parent, false));
        } else {
            return new GameHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_games_friend, parent, false));
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(GameHolder holder, int position) {
        Game game = games.get(position);

        if (getItemViewType(position) == ITEM_TYPE_GAME) {
            holder.usernameTxt.setText("Created by @" + game.getUsername());
        } else {
            SpannableStringBuilder sb = new SpannableStringBuilder("Created by @"
                    + game.getUsername());
            final ForegroundColorSpan fcs = new ForegroundColorSpan(mContext.getResources()
                    .getColor(R.color.primary));
            sb.setSpan(fcs, 11, sb.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            holder.usernameTxt.setText(sb);
        }
        Boolean enabled = game.getAssignedNumber() < game.getTeamsNumber()
                * game.getPlayersNumber();
        holder.joinBtn.setEnabled(enabled);
        holder.joinBtn.setText(enabled ? mContext.getString(R.string.join_games_btn) : mContext.getString(R.string.join_games_full_btn));
        holder.nameTxt.setText(game.getName());
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    OnItemClickListener joinClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(View v, int position) {
            join(position);
        }

        private void join(final int position) {
            final String gameId = games.get(position).getGameId();
            final String name = games.get(position).getName();
            final int teamsNumber = games.get(position).getTeamsNumber();
            final int playersNumber = games.get(position).getPlayersNumber();
            final int cardsNumber = games.get(position).getCardsNumber();
            final int time = games.get(position).getTime();
            final int assignedNumber = games.get(position).getAssignedNumber() + 1;

            if (getItemViewType(position) == ITEM_TYPE_GAME) {
                final int password = games.get(position).getPassword();

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext,
                        R.style.MaterialDialogStyle);
                dialogBuilder.setTitle("Join a Game");

                View dialogView
                        = LayoutInflater.from(mContext).inflate(R.layout.dialog_join_game, null);

                final EditText passwordEditTxt = (EditText) dialogView
                        .findViewById(R.id.join_password_edit_txt);
                final TextInputLayout passwordLayout = (TextInputLayout) dialogView
                        .findViewById(R.id.join_password_layout);

                passwordEditTxt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (passwordEditTxt.getText().toString().length() >= 4) {
                            if (passwordEditTxt.getText().toString().equals(password + "")) {
                                passwordLayout.setError(null);

                                mDialog.cancel();

                                joinGame(gameId, name, teamsNumber, playersNumber, cardsNumber,
                                        time, assignedNumber);
                            } else {
                                passwordLayout.setError("Password is incorrect");
                            }
                        } else {
                            passwordLayout.setError(null);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                passwordLayout.setError(" ");
                passwordLayout.setError(null);

                dialogBuilder.setView(dialogView);
                dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                mDialog = dialogBuilder.create();
                mDialog.show();
            } else {
                joinGame(gameId, name, teamsNumber, playersNumber, cardsNumber, time,
                        assignedNumber);
            }
        }
    };

    private void joinGame(final String gameId, final String name, final Integer teamsNumber,
                          final Integer playersNumber, final Integer cardsNumber,
                          final Integer time, final Integer assignedNumber) {
        final String id = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString("id", "");

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Util.URL_JOIN_GAME, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseJSON = new JSONObject(response);
                    if (responseJSON.getString("result").equals("success")) {
                        String playerId = responseJSON.getString("player_id");
                        mContext.startActivity(new Intent(mContext, JoinLobbyActivity.class)
                                .putExtra(JoinLobbyActivity.EXTRA_GAME_ID, gameId)
                                .putExtra(JoinLobbyActivity.EXTRA_TITLE, name)
                                .putExtra(JoinLobbyActivity.EXTRA_NUMBER_TEAMS, teamsNumber)
                                .putExtra(JoinLobbyActivity.EXTRA_NUMBER_PLAYERS, playersNumber)
                                .putExtra(JoinLobbyActivity.EXTRA_NUMBER_CARDS, cardsNumber)
                                .putExtra(JoinLobbyActivity.EXTRA_TIME, time)
                                .putExtra(JoinLobbyActivity.EXTRA_ASSIGNED_NUMBER, assignedNumber)
                                .putExtra(JoinLobbyActivity.EXTRA_PLAYER_ID, playerId));
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
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, "Server error",
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
                params.put("user_id", id);
                params.put("game_id", gameId);
                return params;
            }


        };
        // Add the request to the RequestQueue.
        Volley.newRequestQueue(mContext).add(stringRequest);
    }
}
package com.moysof.whattheblank;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.moysof.whattheblank.adapter.HostNumberSpinnerAdapter;
import com.moysof.whattheblank.adapter.HostTimeSpinnerAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class HostActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private HostNumberSpinnerAdapter mSpinnerNumberAdapter;
    private HostTimeSpinnerAdapter mSpinnerTimeAdapter;
    private int mGameId;
    private TextInputLayout mNameLayout;
    private TextInputLayout mPasswordLayout;
    private EditText mNameEditTxt;
    private EditText mPasswordEditTxt;
    private ScrollView mScrollView;
    private Spinner mTeamsSpinner;
    private Spinner mPlayersSpinner;
    private Spinner mCardsSpinner;
    private Spinner mTimeSpinner;
    private int mTeamsPos = 0;
    private int mPlayersPos = 0;
    private int mCardsPos = 0;
    private int[] mTimeArray;
    private GoogleApiClient mGoogleApiClient;
    private String mLat;
    private String mLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        TextView idTxt = (TextView) findViewById(R.id.host_id_txt);
        mNameEditTxt = (EditText) findViewById(R.id.host_name_edit_txt);
        mPasswordEditTxt = (EditText) findViewById(R.id.host_pwd_edit_txt);
        mNameLayout = (TextInputLayout) findViewById(R.id.host_name_layout);
        mPasswordLayout = (TextInputLayout) findViewById(R.id.host_pwd_layout);
        mScrollView = (ScrollView) findViewById(R.id.host_scroll_view);
        mTeamsSpinner = (Spinner) findViewById(R.id.host_teams_spinner);
        mPlayersSpinner = (Spinner) findViewById(R.id.host_players_spinner);
        mCardsSpinner = (Spinner) findViewById(R.id.host_cards_spinner);
        mTimeSpinner = (Spinner) findViewById(R.id.host_time_spinner);

        mNameLayout.setError(" ");
        mPasswordLayout.setError(" ");
        mNameLayout.setError(null);
        mPasswordLayout.setError(null);

        findViewById(R.id.join_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initSpinners();

        findViewById(R.id.host_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGame();
            }
        });

        mGameId = generateGameId();
        idTxt.setText(mGameId + "");

        buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (lastLocation != null) {
            mLat = String.valueOf(lastLocation.getLatitude());
            mLng = String.valueOf(lastLocation.getLongitude());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onStart(){
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop(){
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    private void initSpinners() {
        mSpinnerNumberAdapter = new HostNumberSpinnerAdapter(this);
        mTeamsSpinner.setAdapter(mSpinnerNumberAdapter);
        mPlayersSpinner.setAdapter(mSpinnerNumberAdapter);
        mCardsSpinner.setAdapter(mSpinnerNumberAdapter);

        mTimeArray = getResources().getIntArray(R.array.time);
        mSpinnerTimeAdapter = new HostTimeSpinnerAdapter(this, mTimeArray);
        mTimeSpinner.setAdapter(mSpinnerTimeAdapter);

        mTeamsSpinner.setOnItemSelectedListener(mSpinnerItemSelected);
        mPlayersSpinner.setOnItemSelectedListener(mSpinnerItemSelected);
        mCardsSpinner.setOnItemSelectedListener(mSpinnerItemSelected);
    }

    AdapterView.OnItemSelectedListener mSpinnerItemSelected = new AdapterView
            .OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                   int position, long id) {
            if (/*TODO: !isPro &&*/ position >= 3) {
                if (parentView.getId() == R.id.host_teams_spinner) {
                    parentView.setSelection(mTeamsPos);
                } else if (parentView.getId() == R.id.host_players_spinner) {
                    parentView.setSelection(mPlayersPos);
                } else if (parentView.getId() == R.id.host_cards_spinner) {
                    parentView.setSelection(mCardsPos);
                }
                //TODO: Redirect to in-app purchase
                Toast.makeText(HostActivity.this, "Coming soon",
                        Toast.LENGTH_SHORT).show();
            } else {
                if (parentView.getId() == R.id.host_teams_spinner) {
                    mTeamsPos = position;
                } else if (parentView.getId() == R.id.host_players_spinner) {
                    mPlayersPos = position;
                } else if (parentView.getId() == R.id.host_cards_spinner) {
                    mCardsPos = position;
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {
        }

    };

    /**
     * Generate random game id with 6 numbers
     */
    private Integer generateGameId() {
        return new Random().nextInt(999999 - 100000) + 100000;
    }

    private void createGame() {
        Boolean containsErrors = false;

        final String name = mNameEditTxt.getText().toString();
        final String password = mPasswordEditTxt.getText().toString();
        final int time = mTimeArray[mTimeSpinner.getSelectedItemPosition()];

        if (TextUtils.isEmpty(name)) {
            mNameLayout.setError("Game name is required");
            containsErrors = true;
        } else {
            mNameLayout.setError(null);
        }


        if (TextUtils.isEmpty(password)) {
            mPasswordLayout.setError("Game password is required");
            containsErrors = true;
        } else if (password.length() < 4) {
            mPasswordLayout.setError("Password should contain at least 4 numbers");
            containsErrors = true;
        } else {
            mPasswordLayout.setError(null);
        }

        if (containsErrors) {
            mScrollView.smoothScrollTo(0, 0);
        } else {
            final String id = PreferenceManager.getDefaultSharedPreferences(this)
                    .getString("id", "");

            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(this);

            final ProgressDialog progressDialog = ProgressDialog.show(this, "",
                    "Connecting...");

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    Util.URL_CREATE_GAME, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.cancel();
                    try {
                        JSONObject responseJSON = new JSONObject(response);
                        if (responseJSON.getString("result").equals("success")) {
                            startActivity(new Intent(HostActivity.this, HostLobbyActivity.class)
                                    .putExtra(HostLobbyActivity.EXTRA_ID, mGameId + "")
                                    .putExtra(HostLobbyActivity.EXTRA_NAME, name)
                                    .putExtra(HostLobbyActivity.EXTRA_PASSWORD, password)
                                    .putExtra(HostLobbyActivity.EXTRA_NUMBER_TEAMS, mTeamsPos + 1)
                                    .putExtra(HostLobbyActivity.EXTRA_NUMBER_PLAYERS,
                                            mPlayersPos + 1)
                                    .putExtra(HostLobbyActivity.EXTRA_NUMBER_CARDS, mCardsPos + 1)
                                    .putExtra(HostLobbyActivity.EXTRA_NUMBER_TIME, time));
                            finish();
                        } else if (responseJSON.getString("result").equals("empty")) {
                            Toast.makeText(HostActivity.this, "Some fields are empty",
                                    Toast.LENGTH_LONG).show();
                        } else if (responseJSON.getString("result").equals("exists")) {
                            Toast.makeText(HostActivity.this, "Game with this ID already exists",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(HostActivity.this, "Unknown server error",
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        if (Util.isDebugging()) {
                            Toast.makeText(HostActivity.this, "JSON error: " + response,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(HostActivity.this, "Unknown server error",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                    Util.Log(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.cancel();
                    Toast.makeText(HostActivity.this, "Server error",
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
                    params.put("game_id", mGameId + "");
                    params.put("name", name);
                    params.put("password", password);
                    params.put("number_teams", (mTeamsPos + 1) + "");
                    params.put("number_players", (mPlayersPos + 1) + "");
                    params.put("number_cards", (mCardsPos + 1) + "");
                    params.put("number_time", time + "");
                    if (mLat != null && mLng != null) {
                        params.put("lat", mLat);
                        params.put("lng", mLng);
                    }
                    return params;
                }


            };
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        }

    }
}

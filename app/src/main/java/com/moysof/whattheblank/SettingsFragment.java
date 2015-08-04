package com.moysof.whattheblank;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class SettingsFragment extends Fragment {

    private TextView mNameTxt;
    private TextView mUsernameTxt;
    private TextView mPhoneTxt;
    private TextView mEmailTxt;
    private TextInputLayout mOldPasswordLayout;
    private TextInputLayout mNewPasswordLayout;
    private TextInputLayout mRepeatPasswordLayout;
    private View mPasswordLayout;
    private TextView mLogoutTxt;
    private View mProgressBar;
    private AlertDialog mDialog;

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    public SettingsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        mNameTxt = (TextView) rootView.findViewById(R.id.settings_name_txt);
        mUsernameTxt = (TextView) rootView.findViewById(R.id.settings_username_txt);
        mPhoneTxt = (TextView) rootView.findViewById(R.id.settings_phone_txt);
        mEmailTxt = (TextView) rootView.findViewById(R.id.settings_email_txt);
        mPasswordLayout = rootView.findViewById(R.id.settings_password_layout);
        mLogoutTxt = (TextView) rootView.findViewById(R.id.settings_logout_txt);

        mLogoutTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

        loadUserInfo();

        return rootView;
    }

    private void logOut() {
        startActivity(new Intent(getActivity(), LoadingActivity.class));
        getActivity().finish();
    }

    private void loadUserInfo() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mNameTxt.setText(prefs.getString("name", ""));
        mUsernameTxt.setText(prefs.getString("username", ""));
        if (!TextUtils.isEmpty(prefs.getString("phone", ""))) {
            mPhoneTxt.setText(prefs.getString("phone", ""));
        } else {
            ((View) mPhoneTxt.getParent()).setVisibility(View.GONE);
        }
        mEmailTxt.setText(prefs.getString("email", ""));

        if (prefs.getBoolean("is_social", false)) {
            mPasswordLayout.setVisibility(View.GONE);
        } else {
            mPasswordLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPasswordDialog();
                }
            });
        }
    }

    private void showPasswordDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(),
                R.style.MaterialDialogStyle);
        dialogBuilder.setTitle("Change Password");
        View dialogView
                = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_change_password, null);
        final EditText oldEditTxt = (EditText) dialogView
                .findViewById(R.id.settings_password_old_edit_txt);
        final EditText newEditTxt = (EditText) dialogView
                .findViewById(R.id.settings_password_new_edit_txt);
        final EditText repeatEditTxt = (EditText) dialogView
                .findViewById(R.id.settings_password_repeat_edit_txt);

        mOldPasswordLayout = (TextInputLayout) dialogView
                .findViewById(R.id.settings_password_old_layout);
        mNewPasswordLayout = (TextInputLayout) dialogView
                .findViewById(R.id.settings_password_new_layout);
        mRepeatPasswordLayout = (TextInputLayout) dialogView
                .findViewById(R.id.settings_password_repeat_layout);
        mProgressBar = dialogView.findViewById(R.id.settings_password_progress_bar);
        mOldPasswordLayout.setError(" ");
        mNewPasswordLayout.setError(" ");
        mRepeatPasswordLayout.setError(" ");
        mOldPasswordLayout.setError(null);
        mNewPasswordLayout.setError(null);
        mRepeatPasswordLayout.setError(null);

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
                        checkPasswords(oldEditTxt.getText().toString(), newEditTxt.getText()
                                .toString(), repeatEditTxt.getText().toString());
                    }
                });
    }

    private void changePassword(final String password) {
        final String id = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString("id", "");

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Util.URL_CHANGE_PWD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseJSON = new JSONObject(response);
                            if (responseJSON.getString("result").equals("success")) {
                                mDialog.cancel();
                                Toast.makeText(getActivity(), "Password is changed",
                                        Toast.LENGTH_SHORT).show();
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
                        Util.Log(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Server error",
                        Toast.LENGTH_LONG).show();
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
                params.put("password", password);
                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private Boolean mContainsErrors;

    private void checkPasswords(final String oldPassword, final String newPassword,
                                String repeatPassword) {
        mContainsErrors = false;
        if (TextUtils.isEmpty(newPassword)) {
            mNewPasswordLayout.setError("New password is required");
            mContainsErrors = true;
        } else if (newPassword.length() < 6) {
            mNewPasswordLayout.setError("Password should contain at least 6 characters");
            mContainsErrors = true;
        } else if (newPassword.equals(oldPassword)) {
            mNewPasswordLayout.setError("New password should be different");
            mContainsErrors = true;
        } else {
            mNewPasswordLayout.setError(null);
        }

        if (TextUtils.isEmpty(repeatPassword)) {
            mRepeatPasswordLayout.setError("Repeat a new password");
            mContainsErrors = true;
        } else if (!newPassword.equals(repeatPassword)) {
            mRepeatPasswordLayout.setError("Passwords don't match");
            mContainsErrors = true;
        } else {
            mRepeatPasswordLayout.setError(null);
        }

        if (TextUtils.isEmpty(oldPassword)) {
            mOldPasswordLayout.setError("Old password is required");
        } else {
            if (!mContainsErrors) {
                final String id = PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .getString("id", "");

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(getActivity());

                mProgressBar.setVisibility(View.VISIBLE);

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Util.URL_CHECK_PWD,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject responseJSON = new JSONObject(response);
                                    if (responseJSON.getString("result").equals("success")) {
                                        mOldPasswordLayout.setError(null);

                                        changePassword(newPassword);
                                    } else if (responseJSON.getString("result").equals("incorrect")) {
                                        mOldPasswordLayout.setError("Old password is incorrect");
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
                                Util.Log(response);

                                mProgressBar.setVisibility(View.GONE);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Server error",
                                Toast.LENGTH_LONG).show();
                        Util.Log("Server error: " + error);

                        mProgressBar.setVisibility(View.GONE);
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
                        params.put("password", oldPassword);
                        return params;
                    }
                };
                // Add the request to the RequestQueue.
                queue.add(stringRequest);
            }
        }
    }

}
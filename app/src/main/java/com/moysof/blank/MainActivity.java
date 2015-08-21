package com.moysof.blank;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.model.GameRequestContent;
import com.facebook.share.widget.AppInviteDialog;
import com.facebook.share.widget.GameRequestDialog;
import com.moysof.blank.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private CallbackManager mCallbackManager;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_tab_home));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_tab_friends));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_tab_settings));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
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
                return HomeFragment.newInstance();
            } else if (position == 1) {
                return FriendsFragment.newInstance();
            } else {
                return SettingsFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

    }

    public void support(View v) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Util.URL_SUPPORT)));
    }

    public void rate(View v) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="
                + getPackageName())));
    }

    public void purchase(View v) {
        Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
    }

    public void about(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MaterialDialogStyle);
        String version;
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception e) {
            version = "n/a";
        }
        builder.setTitle(getString(R.string.app_name) + " v" + version);
        builder.setView(R.layout.dialog_about);
        builder.setPositiveButton(R.string.settings_about_cancel_btn,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AppCompatDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() != 0) {
            // If user pressed back button, and current tab is not home, then return to the 1st tab
            mViewPager.setCurrentItem(0);
        } else {
            // Otherwise exit app
            super.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void inviteFriends() {
        FacebookSdk.sdkInitialize(this);

        if (AppInviteDialog.canShow()) {

            mCallbackManager = CallbackManager.Factory.create();
            GameRequestDialog requestDialog = new GameRequestDialog(this);
            requestDialog.registerCallback(mCallbackManager,
                    new FacebookCallback<GameRequestDialog.Result>() {
                        public void onSuccess(GameRequestDialog.Result result) {
                            List recipients = result.getRequestRecipients();
                            int recipientsCount = recipients.size();
                            JSONArray recipientsJSON = new JSONArray();
                            for (int i = 0; i < recipientsCount; i++) {
                                recipientsJSON.put(recipients.get(i));
                            }
                            inviteFacebookFriends(recipientsJSON);
                        }

                        public void onCancel() {
                        }

                        public void onError(FacebookException error) {
                        }
                    });

            GameRequestContent content = new GameRequestContent.Builder()
                    .setMessage("Come play WhatTheBlank with me")
                            //.setTo("USER_ID")
                            //.setActionType(GameRequestContent.ActionType.SEND)
                            //.setObjectId("YOUR_OBJECT_ID")
                    .build();
            requestDialog.show(content);
        }
    }

    public void inviteFacebookFriends(final JSONArray recipientsJSON) {
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        final String id = PreferenceManager.getDefaultSharedPreferences(MainActivity.this)
                .getString("id", "");

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Util.URL_INVITE_FACEBOOK_FRIENDS, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseJSON = new JSONObject(response);
                            if (responseJSON.getString("result").equals("success")) {
                            } else {
                                Util.Log("Unknown server error");
                                Toast.makeText(MainActivity.this, "Unknown server error",
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Util.Log("JSON error: " + e);
                            Toast.makeText(MainActivity.this, "JSON error: " + e,
                                    Toast.LENGTH_LONG).show();
                        }
                        Util.Log(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Util.Log("Server error: " + error);
                Toast.makeText(MainActivity.this, "Server error: " + error, Toast.LENGTH_LONG).show();
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
                params.put("recipients", recipientsJSON.toString());
                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

}

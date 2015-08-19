package com.moysof.whattheblank;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.moysof.whattheblank.util.Util;


public class JoinActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private TabLayout mTabLayout;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private GoogleApiClient mGoogleApiClient;
    private RequestQueue mQueue;
    private JoinIDFragment mIdFragment;
    private JoinLocationFragment mLocationFragment;
    private ViewPager mViewPager;
    private boolean mSearchedGames;
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        JoinLocationFragment.sLat = mPrefs.getString("lat", "");
        JoinLocationFragment.sLng = mPrefs.getString("lng", "");

        // Instantiate the RequestQueue.
        mQueue = Volley.newRequestQueue(this);

        buildGoogleApiClient();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        initTabs();

        findViewById(R.id.join_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Add a receiver to listen for creating games
        IntentFilter filter = new IntentFilter();
        filter.addAction(Util.BROADCAST_CREATED_GAME);
        filter.addAction(Util.BROADCAST_CLOSED_GAME);
        registerReceiver(mCreatedReceiver, filter);
    }

    BroadcastReceiver mCreatedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mViewPager.getCurrentItem() == 0) {
                mIdFragment.searchGames();
                mLocationFragment.searchGames();
            } else {
                mLocationFragment.searchGames();
                mIdFragment.searchGames();
            }
        }
    };

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (!mSearchedGames) {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (lastLocation != null) {
                JoinLocationFragment.sLat = String.valueOf(lastLocation.getLatitude());
                JoinLocationFragment.sLng = String.valueOf(lastLocation.getLongitude());

                mPrefs.edit().putString("lat", JoinLocationFragment.sLat)
                        .putString("lng", JoinLocationFragment.sLng).apply();

                mSearchedGames = true;
            }
            mLocationFragment.searchGames();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    private void initTabs() {
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
                mIdFragment = JoinIDFragment.newInstance(mQueue);
                return mIdFragment;
            } else {
                mLocationFragment = JoinLocationFragment.newInstance(mQueue);
                return mLocationFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public String getPageTitle(int position) {
            if (position == 0) {
                return getString(R.string.join_tab_id);
            } else {
                return getString(R.string.join_tab_location);
            }
        }

    }

}

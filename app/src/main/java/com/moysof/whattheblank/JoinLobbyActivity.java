package com.moysof.whattheblank;

import android.support.v7.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;


public class JoinLobbyActivity extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    public static final String EXTRA_GAME_ID = "game_id";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_NUMBER_TEAMS = "number_teams";
    public static final String EXTRA_NUMBER_PLAYERS = "number_players";
    public static final String EXTRA_NUMBER_CARDS = "number_cards";
    public static final String EXTRA_TIME = "time";
    public static final String EXTRA_ASSIGNED_NUMBER = "assigned_number";
    private String mGameId;
    private int mNumberTeams;
    private int mNumberPlayers;
    private int mNumberCards;
    private int mTime;
    private int mAssignedNumber;
    private JoinLobbyGameFragment mGameFragment;
    private JoinLobbyPlayersFragment mPlayersFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        mNumberTeams = getIntent()
                .getIntExtra(EXTRA_NUMBER_TEAMS, 0);
        mNumberPlayers = getIntent()
                .getIntExtra(EXTRA_NUMBER_PLAYERS, 0);
        mNumberCards = getIntent()
                .getIntExtra(EXTRA_NUMBER_CARDS, 0);
        mTime = getIntent()
                .getIntExtra(EXTRA_TIME, 0);
        mAssignedNumber = getIntent()
                .getIntExtra(EXTRA_ASSIGNED_NUMBER, 0);
        mGameId = getIntent()
                .getStringExtra(EXTRA_GAME_ID);

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

        // Add a receiver to listen for joining players, or closing the game by host
        IntentFilter filter = new IntentFilter();
        filter.addAction(HostLobbyActivity.BROADCAST_JOINED_GAME);
        filter.addAction(JoinActivity.BROADCAST_CLOSED_GAME);
        registerReceiver(mJoinedReceiver, filter);
    }

    BroadcastReceiver mJoinedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(HostLobbyActivity.BROADCAST_JOINED_GAME)) {
                // Some other player joined the game
                mAssignedNumber = intent.getIntExtra(HostLobbyActivity.EXTRA_NUMBER_PLAYERS, 0);

                mGameFragment.updateAssignedNumber(mAssignedNumber);
                mPlayersFragment.getPlayers();
            } else {
                // Host has left the game
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(JoinLobbyActivity.this,
                        R.style.MaterialDialogStyle);
                dialogBuilder.setTitle("Error");
                dialogBuilder.setMessage("Host has left the game.");
                dialogBuilder.setCancelable(false);
                dialogBuilder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                });
                dialogBuilder.create().show();
            }
        }
    };

    private void initTabs() {
        ((TextView) findViewById(R.id.join_title_txt)).setText(Html.fromHtml("<b>" + getIntent()
                .getStringExtra(EXTRA_TITLE) + "</b>"));

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
                mGameFragment = JoinLobbyGameFragment.newInstance(mNumberTeams, mNumberPlayers,
                        mNumberCards, mTime, mAssignedNumber);
                return mGameFragment;
            } else {
                mPlayersFragment = JoinLobbyPlayersFragment.newInstance(mGameId);
                return mPlayersFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public String getPageTitle(int position) {
            if (position == 0) {
                return getString(R.string.join_lobby_tab_game);
            } else {
                return getString(R.string.join_lobby_tab_players);
            }
        }

    }

}

package com.moysof.whattheblank;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;

import com.moysof.whattheblank.view.ControllableViewPager;


public class StartGameActivity extends AppCompatActivity {

    public static ControllableViewPager sViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);

        sViewPager = (ControllableViewPager) findViewById(R.id.start_view_pager);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        sViewPager.setAdapter(mSectionsPagerAdapter);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            if (position == 0) {
                return StartReadyFragment.newInstance();
            } else if (position == 4) {
                return StartWaitingFragment.newInstance();
            } else {
                return StartCreateFragment.newInstance(position);
            }
        }

        @Override
        public int getCount() {
            return 5;
        }

    }
}

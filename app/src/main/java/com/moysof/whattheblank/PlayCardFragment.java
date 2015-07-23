package com.moysof.whattheblank;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PlayCardFragment extends Fragment {

    private static int sCardNum;

    public static PlayCardFragment newInstance(int cardNum) {
        sCardNum = cardNum;
        PlayCardFragment fragment = new PlayCardFragment();
        return fragment;
    }

    public PlayCardFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_play_card, container, false);

        View acceptBtn = rootView.findViewById(R.id.start_create_accept_btn);
        acceptBtn.setOnClickListener(mCardClickListener);

        return rootView;
    }

    View.OnClickListener mCardClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            accept();
        }
    };

    private void accept() {
        PlayGameActivity.sViewPager
                .setCurrentItem(PlayGameActivity.sViewPager.getCurrentItem() + 1);
    }
}
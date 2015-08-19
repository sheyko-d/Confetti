package com.moysof.whattheblank;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moysof.whattheblank.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class PlayCardFragment extends Fragment {

    private static int sCardsCount;
    private static PlayGameActivity.Card sCard;
    private static PlayGameActivity sActivity;
    private ImageView mImage;
    private TextView mTimerTxt;
    public static final String EXTRA_TIME = "time";
    private static int sCurrentTeamNum;
    private static int sCurrentRoundNum;
    private TextView mRoundTxt;
    private TextView mCountTxt;
    private Integer mUsedTime = 0;

    public static PlayCardFragment newInstance(int cardsCount, PlayGameActivity.Card card,
                                               int currentTeamNumber,
                                               PlayGameActivity activity, int currentRoundNum) {
        sCardsCount = cardsCount;
        sCard = card;
        sCurrentTeamNum = currentTeamNumber;
        sActivity = activity;
        sCurrentRoundNum = currentRoundNum;

        return new PlayCardFragment();
    }

    public PlayCardFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_play_card, container, false);

        rootView.setBackgroundColor(PlayGameActivity.sTeams.get(sCurrentTeamNum).getColor());

        View acceptBtn = rootView.findViewById(R.id.play_accept_btn);
        mImage = (ImageView) rootView.findViewById(R.id.play_card_img);
        mTimerTxt = (TextView) rootView.findViewById(R.id.play_card_timer_txt);
        mRoundTxt = (TextView) rootView.findViewById(R.id.play_card_round_txt);
        mCountTxt = (TextView) rootView.findViewById(R.id.play_card_count_txt);

        mRoundTxt.setText(Html.fromHtml("<b>ROUND " + (sCurrentRoundNum + 1) + "</b>"));

        mCountTxt.setText(Html.fromHtml("<b>" + PlayGameActivity.sCards.size() + " REMAINING</b>"));

        acceptBtn.setOnClickListener(mCardClickListener);

        initCardImage();

        // Add a receiver to listen for creating games
        IntentFilter filter = new IntentFilter();
        filter.addAction(Util.BROADCAST_TIMER_TICK);
        getActivity().registerReceiver(mReceiver, filter);

        return rootView;
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction().equals(Util.BROADCAST_TIMER_TICK)) {
                    PlayGameActivity.sTime = Util.formatTimer(intent.getIntExtra(EXTRA_TIME, 0));
                    if (intent.getIntExtra(EXTRA_TIME, 0) > 5) {
                        mTimerTxt.setTextColor(getResources().getColor(R.color.primary));
                    } else {
                        mTimerTxt.setTextColor(getResources().getColor(R.color.red));
                        ((Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE))
                                .vibrate(100);

                    }
                    mTimerTxt.setText(PlayGameActivity.sTime);
                    mUsedTime++;
                }
            } catch (Exception e) {
            }
        }
    };

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            PlayGameActivity.sViewPager.setSwipeEnabled(true);
            PlayGameActivity.sViewPager.setSwipeToEdgesEnabled(false);
            if (mTimerTxt != null && !TextUtils.isEmpty(PlayGameActivity.sTime)) {
                mTimerTxt.setText(PlayGameActivity.sTime);
            }
        }
    }

    private void initCardImage() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true).displayer(new FadeInBitmapDisplayer(250, true, false, false))
                .showImageOnLoading(android.R.color.white).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getActivity()).defaultDisplayImageOptions(defaultOptions).build();
        ImageLoader.getInstance().init(config);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(sCard.getImage(), mImage);
    }

    View.OnClickListener mCardClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            accept();
        }
    };

    private void accept() {
        Boolean isLastRound = sCurrentRoundNum == 2;
        Boolean gameIsFinished = true;

        PlayGameActivity.sTeams.get(sCurrentTeamNum).addPoint();

        PlayGameActivity.Card solvedCard = PlayGameActivity.sCards.get((int) (PlayGameActivity
                .sViewPager.getCurrentItem() - 1));

        // If card was already solved, calculate average time
        Boolean updatedTime = false;
        for (int i = 0; i < PlayGameActivity.sSolvedCards.size(); i++) {
            if (PlayGameActivity.sSolvedCards.get(i).getId().equals(solvedCard.getId())) {
                solvedCard.setSolvedTime((PlayGameActivity.sSolvedCards.get(i).getSolvedTime()
                        + mUsedTime) / 2);
                updatedTime = true;
            }
        }

        if (!updatedTime) {
            solvedCard.setSolvedTime(mUsedTime);
            PlayGameActivity.sSolvedCards.add(solvedCard);
        }

        PlayGameActivity.sCards.remove((int) (PlayGameActivity.sViewPager.getCurrentItem() - 1));
        if (PlayGameActivity.sCards.size() > 0) {

            PlayGameActivity.sDontStopTimer = true;
            int currentPos = PlayGameActivity.sViewPager.getCurrentItem();
            PlayGameActivity.sViewPager.setAdapter(PlayGameActivity.sAdapter);
            if (currentPos < PlayGameActivity.sAdapter.getCount() - 1) {
                PlayGameActivity.sViewPager.setCurrentItem(currentPos);
            } else {
                PlayGameActivity.sViewPager.setCurrentItem(currentPos - 1);
            }

            PlayGameActivity.sDontStopTimer = false;
        } else {
            if (!isLastRound) {
                sActivity.switchToFirstTeam();
                sActivity.switchToNextRound();
                gameIsFinished = false;
            }

            if (gameIsFinished) {
                PlayGameActivity.sViewPager.setAdapter(PlayGameActivity.sAdapter);
                PlayGameActivity.sViewPager.setCurrentItem(sCardsCount + 2);
            } else {
                PlayGameActivity.sViewPager.setAdapter(PlayGameActivity.sAdapter);
                PlayGameActivity.sViewPager.setCurrentItem(0);
            }
        }
    }
}
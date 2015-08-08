package com.moysof.whattheblank;

import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moysof.whattheblank.util.Util;

public class PlayWaitingFragment extends Fragment {

    private static PlayGameActivity sActivity;
    private static int sTime;
    private ViewGroup mOrderLayout;
    private TextView mDescTxt;
    private static int sCurrentTeamNumber;

    public static PlayWaitingFragment newInstance(PlayGameActivity activity, int currentTeamNumber,
                                                  int time) {
        sActivity = activity;
        sCurrentTeamNumber = currentTeamNumber;
        sTime = time;

        return new PlayWaitingFragment();
    }

    public PlayWaitingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_play_waiting, container, false);

        mOrderLayout = (ViewGroup) rootView.findViewById(R.id.play_waiting_order_layout);
        mDescTxt = (TextView) rootView.findViewById(R.id.play_waiting_desc_txt);

        int teamsCount = PlayGameActivity.sTeams.size();
        for (int i = 0; i < teamsCount; i++) {
            addTeamCircle(PlayGameActivity.sTeams.get(i).getColor(),
                    PlayGameActivity.sTeams.get(i).getScore());
        }

        Resources res = getResources();
        String teamTxt = "TEAM " + (sCurrentTeamNumber + 1);
        String desc = String.format(res.getString(R.string.play_title_start),
                teamTxt, Util.formatTime(sTime));
        final SpannableStringBuilder sb = new SpannableStringBuilder(desc);

        // Span to set text color to some RGB value
        final ForegroundColorSpan fcs = new ForegroundColorSpan
                (PlayGameActivity.sTeams.get(sCurrentTeamNumber).getColor());

        // Set the text color for first 4 characters
        sb.setSpan(fcs, desc.indexOf(teamTxt), desc.indexOf(teamTxt) + teamTxt.length(),
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        mDescTxt.setText(sb);

        rootView.findViewById(R.id.play_begin_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayGameActivity.sViewPager
                        .setCurrentItem(PlayGameActivity.sViewPager.getCurrentItem() + 1);
                sActivity.startTimer();
            }
        });

        return rootView;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            PlayGameActivity.sViewPager.setSwipeEnabled(false);
            sActivity.stopTimer();
        }
    }

    private void addTeamCircle(int color, int score) {
        View teamView = LayoutInflater.from(getActivity())
                .inflate(R.layout.item_play_team, null);
        // Set height to 36 dp + padding (16 dp vertical or 8 dp horizontal)
        teamView.setLayoutParams(new ViewGroup.LayoutParams(Util.convertDpToPixel(36 + 8),
                Util.convertDpToPixel(36 + 16)));
        ((ImageView) teamView.findViewById(R.id.play_waiting_team_img))
                .setImageDrawable(new ColorDrawable(color));
        ((TextView) teamView.findViewById(R.id.play_waiting_team_score_txt))
                .setText(Html.fromHtml("<b>" + score + "</b>"));
        mOrderLayout.addView(teamView);
    }

}
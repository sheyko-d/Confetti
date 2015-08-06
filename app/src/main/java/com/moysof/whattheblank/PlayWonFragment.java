package com.moysof.whattheblank;

import android.content.res.Resources;
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

public class PlayWonFragment extends Fragment {

    private ViewGroup mOrderLayout;
    private TextView mDescTxt;

    public static PlayWonFragment newInstance() {
        PlayWonFragment fragment = new PlayWonFragment();
        return fragment;
    }

    public PlayWonFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_play_won, container, false);

        mOrderLayout = (ViewGroup) rootView.findViewById(R.id.play_won_order_layout);
        mDescTxt = (TextView) rootView.findViewById(R.id.play_won_desc_txt);

        addTeamCircle(R.color.yellow, 42);
        addTeamCircle(R.color.green, 17);
        addTeamCircle(R.color.blue, 22);

        Resources res = getResources();
        String teamTxt = "TEAM 2";
        String desc = String.format(res.getString(R.string.play_title_won),
                teamTxt);
        final SpannableStringBuilder sb = new SpannableStringBuilder(desc);

        // Span to set text color to some RGB value
        final ForegroundColorSpan fcs = new ForegroundColorSpan(getResources()
                .getColor(R.color.green));

        // Set the text color for first 4 characters
        sb.setSpan(fcs, desc.indexOf(teamTxt), desc.indexOf(teamTxt) + teamTxt.length(),
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        mDescTxt.setText(sb);

        PlayGameActivity.sViewPager.setSwipeEnabled(false);

        rootView.findViewById(R.id.play_again_btn).setOnClickListener(mButtonClickListener);
        rootView.findViewById(R.id.play_quit_btn).setOnClickListener(mButtonClickListener);

        return rootView;
    }

    View.OnClickListener mButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.play_again_btn) {
                playAgain();
            } else {
                quit();
            }
        }
    };

    private void playAgain() {
        PlayGameActivity.sViewPager.setCurrentItem(0);
    }

    private void quit() {
        getActivity().finish();
    }

    private void addTeamCircle(int color, int score) {
        View teamView = LayoutInflater.from(getActivity())
                .inflate(R.layout.item_play_team, null);
        // Set height to 36 dp + padding (16 dp vertical or 8 dp horizontal)
        teamView.setLayoutParams(new ViewGroup.LayoutParams(Util.convertDpToPixel(36 + 8),
                Util.convertDpToPixel(36 + 16)));
        ((ImageView) teamView.findViewById(R.id.play_waiting_team_img)).setImageResource(color);
        ((TextView) teamView.findViewById(R.id.play_waiting_team_score_txt))
                .setText(Html.fromHtml("<b>" + score + "</b>"));
        mOrderLayout.addView(teamView);
    }

}
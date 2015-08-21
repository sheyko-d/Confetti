package com.moysof.blank;

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

import com.moysof.blank.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.json.JSONArray;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlayWaitingFragment extends Fragment {

    private static PlayGameActivity sActivity;
    private static int sTime;
    private ViewGroup mOrderLayout;
    private static int sCurrentTeamNumber;
    private ViewGroup mTeamLayout;

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
        mTeamLayout = (ViewGroup) rootView.findViewById(R.id.play_waiting_team_layout);
        TextView descTxt = (TextView) rootView.findViewById(R.id.play_waiting_desc_txt);
        TextView desc2Txt = (TextView) rootView.findViewById(R.id.play_waiting_desc_2_txt);

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(250, true, false, false))
                .showImageOnLoading(R.drawable.avatar_placeholder)
                .showImageOnFail(R.drawable.avatar_placeholder).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getActivity()).defaultDisplayImageOptions(defaultOptions).build();
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);

        int teamsCount = PlayGameActivity.sTeams.size();
        for (int i = 0; i < teamsCount; i++) {
            addTeamCircle(PlayGameActivity.sTeams.get(i).getColor(),
                    PlayGameActivity.sTeams.get(i).getScore());
        }

        try {
            JSONArray playersJSON = new JSONArray(PlayGameActivity.sTeams.get(sCurrentTeamNumber)
                    .getPlayers());
            int playersCount = playersJSON.length();
            for (int p = 0; p < playersCount; p++) {
                View playerView = LayoutInflater.from(getActivity())
                        .inflate(R.layout.item_play_player, null);
                TextView nameTxt = (TextView) playerView.findViewById(R.id.play_player_name);
                nameTxt.setTextColor(PlayGameActivity.sTeams.get(sCurrentTeamNumber)
                        .getColor());
                nameTxt.setText(playersJSON.getJSONObject(p).getString("name"));
                CircleImageView avatarImg = (CircleImageView) playerView
                        .findViewById(R.id.play_player_avatar);
                avatarImg.setBorderColor(PlayGameActivity.sTeams.get(sCurrentTeamNumber)
                        .getColor());
                avatarImg.setBorderWidth(Util.convertDpToPixel(2));
                imageLoader.displayImage(playersJSON.getJSONObject(p)
                                .getString("avatar"),
                        avatarImg, defaultOptions);
                mTeamLayout.addView(playerView);
            }
        } catch (Exception e) {
            Util.Log("Can't get team players " + e);
        }

        Resources res = getResources();
        String teamTxt = "TEAM " + (sCurrentTeamNumber + 1);
        String desc = String.format(res.getString(R.string.play_title_start),
                teamTxt);
        final SpannableStringBuilder sb = new SpannableStringBuilder(desc);

        // Span to set text color to some RGB value
        final ForegroundColorSpan fcs = new ForegroundColorSpan
                (PlayGameActivity.sTeams.get(sCurrentTeamNumber).getColor());

        // Set the text color for first 4 characters
        sb.setSpan(fcs, desc.indexOf(teamTxt), desc.indexOf(teamTxt) + teamTxt.length(),
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        descTxt.setText(sb);


        String desc2 = String.format(res.getString(R.string.play_title_start_2),
                Util.formatTime(sTime));
        desc2Txt.setText(desc2);

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
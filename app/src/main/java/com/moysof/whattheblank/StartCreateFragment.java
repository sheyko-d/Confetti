package com.moysof.whattheblank;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moysof.whattheblank.view.DrawingView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class StartCreateFragment extends Fragment {

    private static int sCardNum;
    private ImageLoader mImageLoader;
    private View mRootView;
    private DrawingView mDrawingView;

    public static StartCreateFragment newInstance(int cardNum) {
        sCardNum = cardNum;
        StartCreateFragment fragment = new StartCreateFragment();
        return fragment;
    }

    public StartCreateFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_start_create, container, false);

        View acceptBtn = mRootView.findViewById(R.id.start_create_accept_btn);
        View clearBtn = mRootView.findViewById(R.id.start_create_clear_btn);
        mDrawingView = (DrawingView) mRootView.findViewById(R.id.start_create_drawing_view);
        TextView pageNumTxt = (TextView) mRootView.findViewById(R.id.start_create_page_num);
        pageNumTxt.setText("Card " + sCardNum + " of 3");

        acceptBtn.setOnClickListener(mCardClickListener);
        clearBtn.setOnClickListener(mCardClickListener);

        StartGameActivity.sViewPager.setSwipeEnabled(false);

        initAvatar();

        return mRootView;
    }

    View.OnClickListener mCardClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.start_create_accept_btn) {
                accept();
            } else {
                clear();
            }
        }
    };

    private void accept() {
        StartGameActivity.sViewPager
                .setCurrentItem(StartGameActivity.sViewPager.getCurrentItem() + 1);
    }

    private void clear() {
        mDrawingView.clear();
    }

    private void initAvatar() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(250, true, false, false))
                .showImageOnLoading(android.R.color.white)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getActivity()).defaultDisplayImageOptions(defaultOptions).build();
        ImageLoader.getInstance().init(config);
        mImageLoader = ImageLoader.getInstance();

        mImageLoader.displayImage("https://www.gravatar.com/avatar/ba0ebce7e385e4c242c9401ff9d551b2?s=328&d=identicon&r=PG&f=1",
                ((ImageView) mRootView.findViewById(R.id.start_create_avatar_img)));
    }

}
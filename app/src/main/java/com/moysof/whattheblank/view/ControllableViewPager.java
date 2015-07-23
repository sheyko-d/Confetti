package com.moysof.whattheblank.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ControllableViewPager extends ViewPager {

    private boolean mEnabled;

    public ControllableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mEnabled = true;
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (mEnabled) {
            return super.onTouchEvent(event);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(final MotionEvent event) {
        if (mEnabled) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

    public void setSwipeEnabled(Boolean enabled){
        mEnabled = enabled;
    }

}
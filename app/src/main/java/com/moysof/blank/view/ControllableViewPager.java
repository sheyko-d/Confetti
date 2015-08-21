package com.moysof.blank.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.moysof.blank.PlayGameActivity;

public class ControllableViewPager extends ViewPager {

    private boolean mEnabled;
    // Indicates whether you can scroll with finger to first or last page of ViewPager
    private boolean mSwipeToEdgesEnabled;
    private float mInitialXValue = 0;

    public ControllableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mEnabled = true;
        mSwipeToEdgesEnabled = true;
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mInitialXValue = event.getX();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (!mSwipeToEdgesEnabled && (detectSwipeToStart(event) || detectSwipeToEnd(event))) {
                return false;
            }
        }
        if (mEnabled) {
            return super.onTouchEvent(event);
        }

        return false;
    }

    private boolean detectSwipeToStart(MotionEvent event) {
        boolean result = false;

        float diffX = event.getX() - mInitialXValue;
        if (diffX > 0 && getCurrentItem() == 1) {
            result = true;
        }
        return result;
    }

    private boolean detectSwipeToEnd(MotionEvent event) {
        boolean result = false;

        float diffX = event.getX() - mInitialXValue;
        if (diffX < 0 && getCurrentItem() == PlayGameActivity.sCards.size()) {
            result = true;
        }
        return result;
    }

    @Override
    public boolean onInterceptTouchEvent(final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mInitialXValue = event.getX();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (!mSwipeToEdgesEnabled && (detectSwipeToStart(event) || detectSwipeToEnd(event))) {
                return false;
            }
        }
        if (mEnabled) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

    public void setSwipeEnabled(Boolean enabled) {
        mEnabled = enabled;
    }

    public void setSwipeToEdgesEnabled(Boolean enabled) {
        mSwipeToEdgesEnabled = enabled;
    }

}
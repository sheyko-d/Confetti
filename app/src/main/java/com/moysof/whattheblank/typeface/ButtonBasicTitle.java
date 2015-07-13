package com.moysof.whattheblank.typeface;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class ButtonBasicTitle extends Button {

    public ButtonBasicTitle(Context context) {
        super(context);
    }

    public ButtonBasicTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ButtonBasicTitle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                    "fonts/BasicTitleFont.ttf");
            setTypeface(tf);
        }
    }
}
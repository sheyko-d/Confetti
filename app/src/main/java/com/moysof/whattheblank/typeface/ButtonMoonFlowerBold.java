package com.moysof.whattheblank.typeface;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class ButtonMoonFlowerBold extends Button {

    public ButtonMoonFlowerBold(Context context) {
        super(context);
    }

    public ButtonMoonFlowerBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ButtonMoonFlowerBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                    "MoonFlower-Bold.ttf");
            setTypeface(tf);
        }
    }
}
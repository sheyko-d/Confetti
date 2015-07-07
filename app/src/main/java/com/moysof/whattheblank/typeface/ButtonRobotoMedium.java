package com.moysof.whattheblank.typeface;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class ButtonRobotoMedium extends Button {

    public ButtonRobotoMedium(Context context) {
        super(context);
    }

    public ButtonRobotoMedium(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ButtonRobotoMedium(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                    "Roboto-Medium.ttf");
            setTypeface(tf);
        }
    }
}
package com.moysof.whattheblank.typeface;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

public class ButtonRobotoLight extends AppCompatButton {

    public ButtonRobotoLight(Context context) {
        super(context);
    }

    public ButtonRobotoLight(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ButtonRobotoLight(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                    "fonts/Roboto-Light.ttf");
            setTypeface(tf);
        }
    }
}
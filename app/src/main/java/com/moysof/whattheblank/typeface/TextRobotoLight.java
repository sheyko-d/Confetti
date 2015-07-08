package com.moysof.whattheblank.typeface;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextRobotoLight extends TextView {

    public TextRobotoLight(Context context) {
        super(context);
    }

    public TextRobotoLight(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextRobotoLight(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                    "Roboto-Light.ttf");
            setTypeface(tf);
        }
    }
}
package com.moysof.confetti.typeface;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class TextMoonFlower extends AppCompatTextView {

    public TextMoonFlower(Context context) {
        super(context);
    }

    public TextMoonFlower(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextMoonFlower(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                    "fonts/MoonFlower.ttf");
            setTypeface(tf);
        }
    }
}
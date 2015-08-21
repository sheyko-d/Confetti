package com.moysof.blank.typeface;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class TextRobotoLight extends AppCompatTextView {

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
                    "fonts/Roboto-Light.ttf");
            setTypeface(tf);
        }
    }
}
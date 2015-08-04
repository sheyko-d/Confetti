package com.moysof.whattheblank.typeface;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class TextRobotoRegular extends AppCompatTextView {

    public TextRobotoRegular(Context context) {
        super(context);
    }

    public TextRobotoRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextRobotoRegular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                    "fonts/Roboto-Regular.ttf");
            setTypeface(tf);
        }
    }
}
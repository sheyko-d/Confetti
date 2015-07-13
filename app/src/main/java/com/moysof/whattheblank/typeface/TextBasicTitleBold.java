package com.moysof.whattheblank.typeface;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextBasicTitleBold extends TextView {

    public TextBasicTitleBold(Context context) {
        super(context);
    }

    public TextBasicTitleBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextBasicTitleBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                    "fonts/BasicTitleFont.ttf");
            setTypeface(tf);
            setText(Html.fromHtml("<b>" + getText() + "</b>"));
        }
    }
}
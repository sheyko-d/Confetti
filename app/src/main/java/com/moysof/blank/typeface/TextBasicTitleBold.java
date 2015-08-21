package com.moysof.blank.typeface;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.AttributeSet;

public class TextBasicTitleBold extends AppCompatTextView {

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
            Spannable span = new SpannableString(getText());
            span.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, getText().length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            setText(span);
        }
    }
}
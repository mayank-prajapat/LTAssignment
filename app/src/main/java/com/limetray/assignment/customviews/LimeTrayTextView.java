package com.limetray.assignment.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class LimeTrayTextView extends TextView {

    public LimeTrayTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public LimeTrayTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LimeTrayTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf =
                Typeface.createFromAsset(getContext().getAssets(),
                        "fonts/Roboto_Regular.ttf");
        setTypeface(tf);
    }
}

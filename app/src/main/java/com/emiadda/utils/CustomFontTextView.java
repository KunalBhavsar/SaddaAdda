package com.emiadda.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.emiadda.R;

/**
 * Created by Shraddha on 7/8/16.
 */
public class CustomFontTextView extends TextView {

    private static final String TAG = CustomFontTextView.class.getSimpleName();

    public CustomFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public CustomFontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.CustomFontTxtView);
        String customFont = a.getString(R.styleable.CustomFontTxtView_customFont);
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public boolean setCustomFont(Context ctx, String asset) {
        Typeface tf = null;
        try {
            tf = Typeface.createFromAsset(ctx.getAssets(), "fonts/Montserrat-Regular.otf");
        } catch (Exception e) {
            Log.e(TAG, "Error to get typeface: "+e.getMessage());
            return false;
        }
        setTypeface(tf);
        return true;
    }
}

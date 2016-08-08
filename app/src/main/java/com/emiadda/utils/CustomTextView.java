package com.emiadda.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.emiadda.R;

public class CustomTextView extends TextView {

    private String _customFont = null;
    private int _style;

    private ColorStateList mShadowColors;
    private float mShadowDx;
    private float mShadowDy;
    private float mShadowRadius;

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomAttributes(attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomAttributes(attrs);
    }

    /**
     * Set custom attributes, particularly we're setting our font
     *
     * @param attrs
     */
    private void setCustomAttributes(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomTextView);
        _customFont = a.getString(R.styleable.CustomTextView_font);
        if (_customFont != null) {
            if (!isInEditMode()) {
                CustomFontManager fontManager = CustomFontManager.getInstance();
                super.setTypeface(fontManager.getFont(getContext().getAssets(), _customFont), _style);
            }
        }
        a.recycle();
    }

    /**
     * If the user set a style assign it to our _style field so it can be applied to our custom font. Calling super
     * here allows you to use CustomTextView even if you're not changing the font.
     */
    public void setTypeface(Typeface tf, int style) {
        this._style = style;
        super.setTypeface(tf, style);
        return;
    }

    private void updateShadowColor() {
        if (mShadowColors != null) {
            setShadowLayer(mShadowRadius, mShadowDx, mShadowDy, mShadowColors.getColorForState(getDrawableState(), 0));
            invalidate();
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        updateShadowColor();
    }

}
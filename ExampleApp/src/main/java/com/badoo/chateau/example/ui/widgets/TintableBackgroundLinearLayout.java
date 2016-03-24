package com.badoo.chateau.example.ui.widgets;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.TintableBackgroundView;
import android.support.v7.widget.AppCompatBackgroundHelper2;
import android.support.v7.widget.TintManager;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class TintableBackgroundLinearLayout extends LinearLayout implements TintableBackgroundView {

    private final AppCompatBackgroundHelper2 mTintHelper;

    public TintableBackgroundLinearLayout(Context context) {
        this(context, null);
    }

    public TintableBackgroundLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TintableBackgroundLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TintManager tintManager = TintManager.get(getContext());
        mTintHelper = new AppCompatBackgroundHelper2(this, tintManager);
        mTintHelper.loadFromAttributes(attrs, defStyleAttr);
    }

    @Override
    public void setBackgroundResource(int resid) {
        super.setBackgroundResource(resid);
        mTintHelper.onSetBackgroundResource(resid);
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        super.setBackgroundDrawable(background);
        if (mTintHelper != null) {
            mTintHelper.onSetBackgroundDrawable(background);
        }
    }

    @Override
    public void setBackground(Drawable background) {
        super.setBackground(background);
        if (mTintHelper != null) {
            mTintHelper.onSetBackgroundDrawable(background);
        }
    }

    @Override
    public void setSupportBackgroundTintList(ColorStateList tint) {
        if (mTintHelper != null) {
            mTintHelper.setSupportBackgroundTintList(tint);
        }
    }

    @Nullable
    @Override
    public ColorStateList getSupportBackgroundTintList() {
        if (mTintHelper != null) {
            return mTintHelper.getSupportBackgroundTintList();
        }
        else {
            return null;
        }
    }

    @Override
    public void setSupportBackgroundTintMode(@Nullable PorterDuff.Mode tintMode) {
        if (mTintHelper != null) {
            mTintHelper.setSupportBackgroundTintMode(tintMode);
        }
    }

    @Nullable
    @Override
    public PorterDuff.Mode getSupportBackgroundTintMode() {
        if (mTintHelper != null) {
            return mTintHelper.getSupportBackgroundTintMode();
        }
        else {
            return null;
        }
    }
}
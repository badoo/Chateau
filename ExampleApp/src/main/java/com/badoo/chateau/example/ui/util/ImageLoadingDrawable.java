package com.badoo.chateau.example.ui.util;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

import com.facebook.drawee.drawable.DrawableUtils;

public class ImageLoadingDrawable extends Drawable {
    private static final int DEFAULT_SIZE = 100;
    private static final int DEFAULT_WIDTH = 15;
    private static final int DEFAULT_PADDING = 10;

    private final Paint mForegroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF mBounds = new RectF();

    private final int mMinPadding;
    // Size of indicator
    private int mSize;
    // Width of stroke
    private int mWidth;
    private int mLevel = 0;
    private int mColor = 0xFF0080FF;

    public ImageLoadingDrawable(Resources resources) {
        final DisplayMetrics metrics = resources.getDisplayMetrics();
        mSize = dpToPixel(DEFAULT_SIZE, metrics);
        mWidth = dpToPixel(DEFAULT_WIDTH, metrics);
        mMinPadding = dpToPixel(DEFAULT_PADDING, metrics);

        mForegroundPaint.setStrokeWidth(mWidth);
        mForegroundPaint.setStyle(Paint.Style.STROKE);
        mForegroundPaint.setStrokeCap(Paint.Cap.BUTT);
        mForegroundPaint.setColor(mColor);

        mBackgroundPaint.setStrokeWidth(mWidth);
        mBackgroundPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPaint.setColor(mColor);
        mBackgroundPaint.setAlpha((int) (255 * 0.1));
    }

    /**
     * Set size of the progress indicator in pixels
     */
    public void setSize(int size) {
        if (mSize != size) {
            mSize = size;
            invalidateSelf();
        }
    }

    /**
     * Set width of the progress indicator in pixels.
     */
    public void setWidth(int width) {
        if (mWidth != width) {
            mWidth = width;
            invalidateSelf();
        }
    }

    public void setColor(int color) {
        if (mColor != color) {
            mColor = color;
            mForegroundPaint.setColor(mColor);
            mBackgroundPaint.setColor(mColor);
            invalidateSelf();
        }
    }

    @Override
    public void setAlpha(int alpha) {
        mForegroundPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mForegroundPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return DrawableUtils.getOpacityFromColor(mForegroundPaint.getColor());
    }

    @Override
    protected boolean onLevelChange(int level) {
        mLevel = level;
        invalidateSelf();
        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        if (mLevel == 0 || mLevel == 10000) {
            return; // Don't draw with no progress
        }
        calcBounds();

        canvas.drawArc(mBounds, 0, 360, false, mBackgroundPaint);
        canvas.drawArc(mBounds, -90, calcAngle(), false, mForegroundPaint);
    }

    private void calcBounds() {
        final Rect canvasBounds = getBounds();
        final float smallestDimension = Math.min(canvasBounds.width(), canvasBounds.height());

        final float width;
        final float bound;
        final float totalSpinnerSize = mSize + mWidth + 2 * mMinPadding;
        if (smallestDimension < totalSpinnerSize) {
            width = mWidth * (smallestDimension / totalSpinnerSize);
            bound = ((smallestDimension - mWidth) / 2.0f) - mMinPadding;
        } else {
            bound = mSize / 2.0f;
            width = mWidth;
        }
        mForegroundPaint.setStrokeWidth(width);
        mBackgroundPaint.setStrokeWidth(width);

        int centerX = canvasBounds.centerX();
        int centerY = canvasBounds.centerY();
        mBounds.set(centerX - bound, centerY - bound, centerX + bound, centerY + bound);
    }

    private float calcAngle() {
        return (mLevel * 360) / 10000;
    }


    private int dpToPixel(int dp, @NonNull DisplayMetrics metrics) {
        return (int) (dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

}

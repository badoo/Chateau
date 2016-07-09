package com.badoo.chateau.extras.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.badoo.chateau.utils.R;

/**
 * Layout providing chrome around a message bubble.  This view draws the message chrome over the views so it's important to have the correct
 * padding for items such as text messages where losing the corner to chrome would cause and issue.
 */
public class ChatBubbleLayout extends FrameLayout {

    private final static PorterDuffXfermode DST_IN = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private NinePatchDrawable mTailMask;
    private NinePatchDrawable mNoTailMask;
    private NinePatchDrawable mMask;

    private boolean mShowTail = true;
    private boolean mReverseLayout;

    public ChatBubbleLayout(Context context) {
        this(context, null);
    }

    public ChatBubbleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChatBubbleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ChatBubbleLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        setLayerType(LAYER_TYPE_HARDWARE, mPaint);

        mMask = mTailMask = createMask(R.drawable.ic_mess_bubble_right);
        mNoTailMask = createMask(R.drawable.ic_mess_bubble_circle);
    }

    private NinePatchDrawable createMask(@DrawableRes int res) {
        final Bitmap maskBitmap = BitmapFactory.decodeResource(getResources(), res);
        final NinePatch patch = new NinePatch(maskBitmap, maskBitmap.getNinePatchChunk(), "BubbleMask");
        return new NinePatchDrawable(getResources(), patch);
    }

    /**
     * By default the tail is on the rhs, set reverse layout to <code>true</code> to have the tail on the other side.
     */
    public void reverseLayout(boolean reverseLayout) {
        if (mReverseLayout == reverseLayout) {
            return;
        }

        mReverseLayout = reverseLayout;
        invalidate();
    }

    public void showTail(boolean showTail) {
        if (mShowTail == showTail) {
            return;
        }

        mShowTail = showTail;
        mMask = mShowTail ? mTailMask : mNoTailMask;
        invalidate();
    }


    @Override
    protected int getSuggestedMinimumWidth() {
        return Math.max(mMask.getMinimumWidth(), super.getSuggestedMinimumWidth());
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return Math.max(mMask.getMinimumHeight(), super.getSuggestedMinimumHeight());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (w != oldw || h != oldh) {
            mTailMask.setBounds(0, 0, w, h);
            mNoTailMask.setBounds(0, 0, w, h);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        mMask.getPaint().setXfermode(DST_IN);

        if (mReverseLayout && mShowTail) {
            canvas.save();
            canvas.scale(-1.0f, 1.0f);
            canvas.translate(-getMeasuredWidth(), 0);
        }
        mMask.draw(canvas);
        if (mReverseLayout && mShowTail) {
            canvas.restore();
        }
        mMask.getPaint().setXfermode(null);
    }
}

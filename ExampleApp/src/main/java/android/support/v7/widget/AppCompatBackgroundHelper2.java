package android.support.v7.widget;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Extends AppCompatBackgroundHelper in the support v7 library to increase the visibility of the class.
 * Will most likely break when the extended class changes so we need to keep an eye on it.
 */
public class AppCompatBackgroundHelper2 extends AppCompatBackgroundHelper {

    public AppCompatBackgroundHelper2(View view, TintManager tintManager) {
        super(view, tintManager);
    }

    @Override
    public void loadFromAttributes(AttributeSet attrs, int defStyleAttr) {
        super.loadFromAttributes(attrs, defStyleAttr);
    }

    @Override
    public void onSetBackgroundDrawable(Drawable background) {
        super.onSetBackgroundDrawable(background);
    }

    @Override
    public void onSetBackgroundResource(int resId) {
        super.onSetBackgroundResource(resId);
    }

    @Override
    public void setSupportBackgroundTintMode(PorterDuff.Mode tintMode) {
        super.setSupportBackgroundTintMode(tintMode);
    }

    @Override
    public PorterDuff.Mode getSupportBackgroundTintMode() {
        return super.getSupportBackgroundTintMode();
    }

    @Override
    public void setSupportBackgroundTintList(ColorStateList tint) {
        super.setSupportBackgroundTintList(tint);
    }

    @Override
    public ColorStateList getSupportBackgroundTintList() {
        return super.getSupportBackgroundTintList();
    }
}

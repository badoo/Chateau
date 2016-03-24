package com.badoo.barf.mvp;

import android.support.annotation.NonNull;

/**
 * Base view class containing functionality for handling attaching a Presenter to the View.
 */
public class BaseView<P extends Presenter> implements View<P> {

    private P mPresenter;

    @Override
    public final void attachPresenter(@NonNull P presenter) {
        mPresenter = presenter;
        onPresenterAttached(mPresenter);
    }

    /**
     * Invoked after the presenter is attached to the View. Any initial interaction with the Presenter can be done here in extending classes.
     */
    protected void onPresenterAttached(@NonNull P presenter) {
    }

    /**
     * returns the attached presenter.
     */
    @NonNull
    protected P getPresenter() {
        return mPresenter;
    }
}

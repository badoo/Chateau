package com.badoo.barf.mvp;


import android.support.annotation.NonNull;

/**
 * This class provides an abstraction between the view and the presenter by allowing the View implementation to defer the creation of
 * it's presenter to this factory class.
 */
public class PresenterFactory<View extends MvpView, Presenter extends MvpPresenter> {

    private final PresenterFactoryDelegate<View, Presenter> mDelegate;
    private Presenter mPresenter;

    public PresenterFactory(@NonNull PresenterFactoryDelegate<View, Presenter> delegate) {
        mDelegate = delegate;
    }

    /**
     * Initialise the presenter, this method must only be called once per factory.  If the presenter instance is required after it is
     * initialised then it can be retrieved by {@link #init(MvpView)}.  If this method is called multiple times an
     * {@link IllegalStateException} will be thrown.
     */
    @NonNull
    public Presenter init(@NonNull View v) {
        if (mPresenter != null) {
            throw new IllegalStateException("Presenter is already initialised " + mPresenter);
        }
        mPresenter = mDelegate.create(v);
        return mPresenter;
    }

    /**
     * Returns to initialised presenter.  If the presenter hasn't been initialised by a call to {@link #init(MvpView)} an
     * {@link IllegalStateException} will be thrown.
     */
    @NonNull
    public Presenter get() {
        if (mPresenter == null) {
            throw new IllegalStateException("Presenter hasn't been initialised, a call must be made to #init(View) before this method is called");
        }
        return mPresenter;
    }

    /**
     * Interface that can be passed to the constructor of the presenter factory to allow lambdas to be used when creating the factory.
     */
    public interface PresenterFactoryDelegate<View extends MvpView, Presenter extends MvpPresenter> {
        @NonNull
        Presenter create(@NonNull View v);
    }
}

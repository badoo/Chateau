package com.badoo.barf.mvp;

import android.support.annotation.NonNull;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * A base class for presenters to provider some utility functionality.  When using this presenter it's important that the lifecycle methods
 * are called at the relevant times.
 */
public abstract class BasePresenter<V extends View, F extends Presenter.FlowListener> implements Presenter<V, F> {

    private final CompositeSubscription mTrackedSubscriptions = new CompositeSubscription();
    private V mView;
    private F mFlowListener;

    /**
     * Add a subscription to be tracked such that when this presenter is destroyed the subscription will be unsubscribed from.
     */
    public Subscription trackSubscription(Subscription subscription) {
        mTrackedSubscriptions.add(subscription);
        return subscription;
    }

    @Override
    public void attachView(@NonNull V view) {
        mView = view;
    }

    @Override
    public void attachFlowListener(@NonNull F flowListener) {
        mFlowListener = flowListener;
    }

    /**
     * Get the flow listener if one is attached.
     */
    protected F getFlowListener() {
        return mFlowListener;
    }

    /**
     * Get the attached view.
     */
    protected V getView() {
        return mView;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
    }

    /**
     * Unsubscribes any tracked subscriptions.
     */
    @Override
    public void destroy() {
        mTrackedSubscriptions.unsubscribe();
    }

}

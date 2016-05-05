package com.badoo.barf.mvp;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * A base class for presenters to provider some utility functionality to manage rx subscriptions.  When using this presenter it's important
 * that the lifecycle methods are called at the relevant times.
 */
public abstract class BaseRxPresenter implements MvpPresenter {

    private final CompositeSubscription mTrackedSubscriptions = new CompositeSubscription();

    /**
     * Add a subscription to be tracked such that when this presenter is destroyed the subscription will be unsubscribed from.
     */
    public Subscription trackSubscription(Subscription subscription) {
        mTrackedSubscriptions.add(subscription);
        return subscription;
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

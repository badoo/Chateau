package com.badoo.barf.mvp;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * A base class for presenters to provider some utility functionality to manage rx subscriptions.  When using this presenter it's important
 * that the lifecycle methods are called at the relevant times.
 */
public abstract class BaseRxPresenter implements MvpPresenter {

    private List<Subscription> mSubscriptions = new ArrayList<>();

    /**
     * Add a subscription to be tracked such that when this presenter is destroyed the subscription will be unsubscribed from.
     */
    public void manage(Subscription subscription) {
        mSubscriptions.add(subscription);
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
        for (Subscription subscription : mSubscriptions) {
            subscription.unsubscribe();
        }
        mSubscriptions.clear();
    }

}

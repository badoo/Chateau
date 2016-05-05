package com.badoo.barf.usecase;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Base class that should be used for all user cases, ensure subscriptions to the observable returned view {@link #execute(Object)} are
 * published to on the main thread, and any work done occurs on a computation thread.
 */
public abstract class UseCaseOld<P, R> {

    public Observable<R> execute(P params) {
        return createObservable(params)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.computation());
    }

    /**
     * Create a observer which will publish the result of the use case for the given params.
     */
    protected abstract Observable<R> createObservable(P params);

    /**
     * Can be used params type is no params are required.
     */
    public enum NoParams {
        NONE
    }

}

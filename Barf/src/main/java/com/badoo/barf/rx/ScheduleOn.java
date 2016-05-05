package com.badoo.barf.rx;

import rx.Observable.Transformer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ScheduleOn {

    /**
     * Applies schedules such that the subscription is performed on the io thread, but the response is observed on the Android main thread.
     */
    public static <T> Transformer<T, T> io() {
        return observable -> observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }
}

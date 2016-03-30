package com.badoo.unittest.rx;

import android.support.annotation.NonNull;

import java.util.concurrent.Executors;

import rx.Scheduler;
import rx.schedulers.Schedulers;

public class TrackingSchedulerFactory implements SchedulerFactory {

    private final ThreadGroup mIOThreadGroup = new ThreadGroup("ioThreadGroup");
    private final ThreadGroup mComputationThreadGroup = new ThreadGroup("computationGroup");
    private final ThreadGroup mNewThreadGroup = new ThreadGroup("newThreadGroup");
    private final ThreadGroup mMainThreadGroup = new ThreadGroup("mainThreadGroup");

    @Override
    public final Scheduler createIOScheduler() {
        return Schedulers.from(Executors.newSingleThreadExecutor(r -> new Thread(mIOThreadGroup, r)));
    }

    @Override
    public final Scheduler createComputationScheduler() {
        return Schedulers.from(Executors.newSingleThreadExecutor(r -> new Thread(mComputationThreadGroup, r)));
    }

    @Override
    public Scheduler createNewThreadScheduler() {
        return Schedulers.from(Executors.newSingleThreadExecutor(r -> new Thread(mNewThreadGroup, r)));
    }

    @Override
    public final Scheduler createMainThreadScheduler() {
        return Schedulers.from(Executors.newSingleThreadExecutor(r -> new Thread(mMainThreadGroup, r)));
    }

    public final boolean isOnIOScheduler(@NonNull Thread thread) {
        return thread.getThreadGroup() == mIOThreadGroup;
    }

    public final boolean isOnComputationScheduler(@NonNull Thread thread) {
        return thread.getThreadGroup() == mComputationThreadGroup;
    }

    public final boolean isOnNewThreadScheduler(@NonNull Thread thread) {
        return thread.getThreadGroup() == mNewThreadGroup;
    }

    public final boolean isOnMainScheduler(@NonNull Thread thread) {
        return thread.getThreadGroup() == mMainThreadGroup;
    }
}

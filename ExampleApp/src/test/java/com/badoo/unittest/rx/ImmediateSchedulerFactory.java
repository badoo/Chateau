package com.badoo.unittest.rx;

import rx.Scheduler;
import rx.schedulers.Schedulers;

public class ImmediateSchedulerFactory implements SchedulerFactory {
    @Override
    public Scheduler createIOScheduler() {
        return Schedulers.immediate();
    }

    @Override
    public Scheduler createComputationScheduler() {
        return Schedulers.immediate();
    }

    @Override
    public Scheduler createNewThreadScheduler() {
        return Schedulers.immediate();
    }

    @Override
    public Scheduler createMainThreadScheduler() {
        return Schedulers.immediate();
    }
}

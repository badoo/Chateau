package com.badoo.unittest.rx;

import rx.Scheduler;

public interface SchedulerFactory {
    Scheduler createIOScheduler();

    Scheduler createComputationScheduler();

    Scheduler createNewThreadScheduler();

    Scheduler createMainThreadScheduler();
}

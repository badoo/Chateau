package com.badoo.unittest.rx;

import android.support.annotation.Nullable;

import java.util.concurrent.TimeUnit;

import rx.Scheduler;
import rx.schedulers.TestScheduler;

public class TestSchedulerFactory implements SchedulerFactory {

    @Nullable
    private TestScheduler mIOScheduler;
    @Nullable
    private TestScheduler mComputationScheduler;
    @Nullable
    private TestScheduler mNewThreadScheduler;
    @Nullable
    private TestScheduler mMainThreadScheduler;

    @Override
    public Scheduler createIOScheduler() {
        mIOScheduler = new TestScheduler();
        return mIOScheduler;
    }

    @Override
    public Scheduler createComputationScheduler() {
        mComputationScheduler = new TestScheduler();
        return mComputationScheduler;
    }

    @Override
    public Scheduler createNewThreadScheduler() {
        mNewThreadScheduler = new TestScheduler();
        return mNewThreadScheduler;
    }

    @Override
    public Scheduler createMainThreadScheduler() {
        mMainThreadScheduler = new TestScheduler();
        return mMainThreadScheduler;
    }

    @Nullable
    public TestScheduler getIOScheduler() {
        return mIOScheduler;
    }

    @Nullable
    public TestScheduler getComputationScheduler() {
        return mComputationScheduler;
    }

    @Nullable
    public TestScheduler getNewThreadScheduler() {
        return mNewThreadScheduler;
    }

    @Nullable
    public TestScheduler getMainThreadScheduler() {
        return mMainThreadScheduler;
    }

    public void advanceTimeBy(long delayTime, TimeUnit unit) {
        if (mIOScheduler != null) mIOScheduler.advanceTimeBy(delayTime, unit);
        if (mComputationScheduler != null) mComputationScheduler.advanceTimeBy(delayTime, unit);
        if (mNewThreadScheduler != null) mNewThreadScheduler.advanceTimeBy(delayTime, unit);
        if (mMainThreadScheduler != null) mMainThreadScheduler.advanceTimeBy(delayTime, unit);
    }


    public void advanceTimeTo(long delayTime, TimeUnit unit) {
        if (mIOScheduler != null) mIOScheduler.advanceTimeTo(delayTime, unit);
        if (mComputationScheduler != null) mComputationScheduler.advanceTimeTo(delayTime, unit);
        if (mNewThreadScheduler != null) mNewThreadScheduler.advanceTimeTo(delayTime, unit);
        if (mMainThreadScheduler != null) mMainThreadScheduler.advanceTimeTo(delayTime, unit);
    }

    public void triggerActions() {
        if (mIOScheduler != null) mIOScheduler.triggerActions();
        if (mComputationScheduler != null) mComputationScheduler.triggerActions();
        if (mNewThreadScheduler != null) mNewThreadScheduler.triggerActions();
        if (mMainThreadScheduler != null) mMainThreadScheduler.triggerActions();
    }

    /**
     * Advance all schedulers by 1ms
     */
    public void tick() {
        advanceTimeBy(1, TimeUnit.MILLISECONDS);
    }

}

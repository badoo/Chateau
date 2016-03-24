package com.badoo.unittest.rx;

import android.support.annotation.NonNull;

import org.junit.After;
import org.junit.Before;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.plugins.RxTestSchedulerProxy;

import static org.junit.Assert.assertTrue;

/**
 * Base unit test to track the schedulers calls are made upon.
 */
public class BaseRxTestCase {

    private static final String ASSERT_TEXT = "Result not returned on %s scheduler [%s]";

    private static final TrackingSchedulerFactory SCHEDULER_FACTORY = new TrackingSchedulerFactory();

    @Before
    public void beforeTest() {
        RxTestSchedulerProxy.getInstance().setSchedulerFactory(SCHEDULER_FACTORY);
    }

    @After
    public void afterTest() {
        RxTestSchedulerProxy.getInstance().reset();
    }

    protected TrackingSchedulerFactory getSchedulerFactory() {
        return SCHEDULER_FACTORY;
    }

    protected <T> TestSubscriber<T> executeTarget(Observable<T> result) {
        final TestSubscriber<T> testSubscriber = new TestSubscriber<>();
        result.doOnError(Throwable::printStackTrace)
            .subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();
        return testSubscriber;
    }

    protected void assertOnIOScheduler(@NonNull Thread thread) {
        assertTrue(String.format(ASSERT_TEXT, "IO", thread.getThreadGroup().getName()),
            getSchedulerFactory().isOnIOScheduler(thread));
    }

    protected void assertOnComputationScheduler(@NonNull Thread thread) {
        assertTrue(String.format(ASSERT_TEXT, "Computation", thread.getThreadGroup().getName()),
            getSchedulerFactory().isOnComputationScheduler(thread));
    }

    protected void assertOnNewThreadScheduler(@NonNull Thread thread) {
        assertTrue(String.format(ASSERT_TEXT, "NewThread", thread.getThreadGroup().getName()),
            getSchedulerFactory().isOnNewThreadScheduler(thread));
    }

    protected void assertOnMainThreadScheduler(@NonNull Thread thread) {
        assertTrue(String.format(ASSERT_TEXT, "MainThread", thread.getThreadGroup().getName()),
            getSchedulerFactory().isOnMainScheduler(thread));
    }

}

package com.badoo.unittest.rx;

import org.junit.After;
import org.junit.Before;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.plugins.RxTestSchedulerProxy;

/**
 * Base unit test to track the schedulers calls are made upon.
 */

public class BaseRxTestCase {

    private static final String ASSERT_TEXT = "Result not returned on %s scheduler [%s]";

    private SchedulerFactory mSchedulerFactory = new ImmediateSchedulerFactory();

    @Before
    public void beforeTest() {
        setModeDefault();
    }

    @After
    public void afterTest() {
        RxTestSchedulerProxy.getInstance().reset();
    }

    public ImmediateSchedulerFactory setModeDefault() {
        mSchedulerFactory = new ImmediateSchedulerFactory();
        RxTestSchedulerProxy.getInstance().setSchedulerFactory(mSchedulerFactory);
        return (ImmediateSchedulerFactory) mSchedulerFactory;
    }

    public TrackingSchedulerFactory setModeTracking() {
        throw new UnsupportedOperationException("Tracking mode is not current available");
        //RxTestSchedulerProxy.getInstance().setSchedulerFactory(mSchedulerFactory);
    }

    public TestSchedulerFactory setModeTest() {
        mSchedulerFactory = new TestSchedulerFactory();
        RxTestSchedulerProxy.getInstance().setSchedulerFactory(mSchedulerFactory);
        return (TestSchedulerFactory) mSchedulerFactory;
    }

    protected <T extends SchedulerFactory> T getSchedulerFactory(Class<T> type) {
        if (!type.isAssignableFrom(mSchedulerFactory.getClass())) {
            throw new IllegalArgumentException("Unexpected type, current scheduler factory is of type " + mSchedulerFactory.getClass().getName());
        }
        //noinspection unchecked
        return (T) mSchedulerFactory;
    }

    protected <T> TestSubscriber<T> executeTarget(Observable<T> result) {
        final TestSubscriber<T> testSubscriber = new TestSubscriber<>();
        result.doOnError(Throwable::printStackTrace)
            .subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();
        return testSubscriber;
    }
}

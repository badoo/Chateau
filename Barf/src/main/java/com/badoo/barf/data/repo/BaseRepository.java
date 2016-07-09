package com.badoo.barf.data.repo;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;

/**
 * Implementation of {@link Repository} which manages in progress queries.  For this to work correctly there are two constraints, firstly
 * the query must correctly implemented the equals method such that it can be compared to in progress queries.  Secondly the Observable
 *
 * returned should be able to handle multiple subscription without causing long running operations such as network calls to restart.
 */
public abstract class BaseRepository<T> implements Repository<T> {

    private static final String TAG = BaseRepository.class.getSimpleName();
    private static final boolean DEBUG = true;

    private Map<Query<?>, Pending> mInProcessQueries = new ConcurrentHashMap<>();

    @NonNull
    @Override
    public <Result> Observable<Result> query(@NonNull Query<Result> query) {
        Log.d(TAG, "Starting query: " + query + " with pending: " + mInProcessQueries.size());
        if (DEBUG) {
            assertQueryOverridesEqualsAndHashcode(query.getClass());
        }

        Pending pending = mInProcessQueries.get(query);
        if (pending != null) {
            Log.d(TAG, "Query: " + query + " already in progress for " + (System.currentTimeMillis() - pending.mStartTime) + " ms, ignoring");
            //noinspection unchecked
            return (Observable<Result>) pending.mObservable;
        }

        final Observable<Result> queryObservable = createObservable(query)
            .doOnSubscribe(() -> {
                Pending current = mInProcessQueries.get(query);
                if (current != null) {
                    current.mSubscriptionCount++;
                    if (DEBUG) {
                        Log.d(TAG, "Query: " + query + " subscribe, count: " + current.mSubscriptionCount);
                    }
                }
            })
            .doOnUnsubscribe(() -> {
                Pending current = mInProcessQueries.get(query);
                if (current != null) {
                    current.mSubscriptionCount--;
                    if (current.mSubscriptionCount == 0) {
                        terminateQuery(query);
                        if (DEBUG) {
                            Log.d(TAG, "Query: " + query + " unsubscribe, count: " + current.mSubscriptionCount);
                        }
                    }
                }
            })
            .doOnTerminate(() -> {
                terminateQuery(query);
            });
        mInProcessQueries.put(query, new Pending(queryObservable, System.currentTimeMillis()));
        return queryObservable;
    }

    private <Result> void terminateQuery(@NonNull Query<Result> query) {
        Pending terminated = mInProcessQueries.get(query);
        if (terminated != null) {
            Log.d(TAG, "Query: " + query + " terminated after " + (System.currentTimeMillis() - terminated.mStartTime) + "ms");
            mInProcessQueries.remove(query);
        }
    }

    /**
     * In the case an query isn't in progress, this method will be called to perform the query.
     */
    @NonNull
    protected abstract <Result> Observable<Result> createObservable(@NonNull Query<Result> query);

    @VisibleForTesting
    static boolean assertQueryOverridesEqualsAndHashcode(Class<?> clazz) {
        try {
            final Method hashCode = clazz.getMethod("hashCode");
            final Method equals = clazz.getMethod("equals", Object.class);
            return hashCode.getDeclaringClass() == clazz && equals.getDeclaringClass() == clazz;
        }
        catch (NoSuchMethodException e) {
            // Should be impossible.
            throw new RuntimeException();
        }
    }

    private static class Pending {

        final Observable<?> mObservable;
        final long mStartTime;
        int mSubscriptionCount;

        private Pending(Observable<?> observable, long startTime) {
            mObservable = observable;
            mStartTime = startTime;
        }
    }

}

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
 * returned should be able to handle multiple subscription without causing long running operations such as network calls to restart.
 */
public abstract class BaseRepository<T> implements Repository<T> {

    private static final String TAG = BaseRepository.class.getSimpleName();
    private static final boolean DEBUG = true;

    private Map<Query<?>, Observable<?>> mInProcessQueries = new ConcurrentHashMap<>();

    @NonNull
    @Override
    public <Result> Observable<Result> query(@NonNull Query<Result> query) {
        Log.d(TAG, "Starting query: " + query);
        if (DEBUG) {
            assertQueryOverridesEqualsAndHashcode(query.getClass());
        }

        Observable<?> inProcessQuery = mInProcessQueries.get(query);
        if (inProcessQuery != null) {
            Log.d(TAG, "Query: " + query + " already in progress, ignoring");
            //noinspection unchecked
            return (Observable<Result>) inProcessQuery;
        }

        final Observable<Result> queryObservable = createObservable(query)
            .doOnTerminate(() -> {
                Log.d(TAG, "Query: " + query + " terminated");
                mInProcessQueries.remove(query);
            });

        mInProcessQueries.put(query, queryObservable);
        return queryObservable;
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

}

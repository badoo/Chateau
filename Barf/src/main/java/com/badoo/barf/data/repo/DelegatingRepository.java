package com.badoo.barf.data.repo;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;

/**
 * A delegating repository allows for mapping between query types and handlers for those queries.  This can be done manually, but it's
 * recommended that this class is used alongside the {@link com.badoo.barf.data.repo.annotations.Handles} annotation.
 *
 * @see com.badoo.barf.data.repo.annotations.Handles
 * @see com.badoo.barf.data.repo.annotations.HandlesUtil
 */
public class DelegatingRepository<T> extends BaseRepository<T> {

    public final Map<Class<?>, QueryHandler<?, ?>> mRegisteredHandlers = new HashMap<>();

    /**
     * Register a given query type against a handler.
     */
    public <Result> void registerHandler(Class<Query<Result>> type, QueryHandler<Query<Result>, Result> handler) {
        mRegisteredHandlers.put(type, handler);
    }

    @NonNull
    @Override
    protected <Result> Observable<Result> createObservable(@NonNull Query<Result> query) {
        final QueryHandler handler = mRegisteredHandlers.get(query.getClass());
        if (handler == null) {
            throw new IllegalArgumentException(String.format("No handler is registered for query %s", query.getClass()));
        }
        // Safe due to check at #registerHandler
        //noinspection unchecked
        return handler.handleQuery(query);
    }


    public interface QueryHandler<Q extends Query, R> {

        Observable<R> handleQuery(Q query);
    }


}

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
public class DelegatingRepository<Q extends Query, R> extends BaseRepository<Q, R> {

    public final Map<Class<?>, QueryHandler<?, ?>> mRegisteredHandlers = new HashMap<>();

    /**
     * Register a given query type against a handler.
     */
    public void registerHandler(Class<Q> type, QueryHandler<Q, R> handler) {
        mRegisteredHandlers.put(type, handler);
    }

    @NonNull
    @Override
    protected Observable<R> createObservable(@NonNull Q query) {
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

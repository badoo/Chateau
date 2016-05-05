package com.badoo.barf.data.repo.annotations;

import com.badoo.barf.data.repo.DelegatingRepository;
import com.badoo.barf.data.repo.Query;

import java.lang.reflect.Method;

import rx.Observable;

public class HandlesUtil {

    /**
     * Register a data source annotated with the {@link Handles} annotation with an instance of {@link DelegatingRepository}.  All the methods
     * on the data source which are annotated must have a return type matching the return type of the repository or <code>void</code>
     */
    public static void registerHandlersFromAnnotations(DelegatingRepository repo, Object annotatedDataSource) {
        for (Class<?> clazz : annotatedDataSource.getClass().getInterfaces()) {
            processClass(repo, clazz, annotatedDataSource);
        }
    }

    private static void processClass(DelegatingRepository<?> repo, Class<?> clazz, Object annotatedDataSource) {
        for (final Method method : clazz.getDeclaredMethods()) {
            final Handles handles = method.getAnnotation(Handles.class);
            if (handles == null) {
                continue;
            }

            //noinspection unchecked
            final Class queryType = (Class) handles.value();
            final DelegatingRepository.QueryHandler handler = createHandlerWithReturn(annotatedDataSource, method);
            //noinspection unchecked
            repo.registerHandler(queryType, handler);
        }
    }

    private static <Q extends Query, R> DelegatingRepository.QueryHandler<Q, R> createHandlerWithReturn(Object target, Method handlingMethod) {
        return query -> {
            try {
                //noinspection unchecked
                return (Observable<R>) handlingMethod.invoke(target, query);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}

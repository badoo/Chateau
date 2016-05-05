package com.badoo.barf.data.repo;

import android.support.annotation.NonNull;

import rx.Observable;

/**
 * A repository is a data store that can be used to both request and publish data.  The repository may or may not be able to retrieve data
 * from local and/or remote sources, but if so, it should follow the mode specified by the {@link Query#getMode()}.
 */
public interface Repository<DataType> {

    /**
     * Perform a query on the repository.
     *
     * @return an {@link Observable} that will only be completed one all the necessary information has been loaded based upon the
     * {@link Query#getMode()}.
     */
    @NonNull
    <Result> Observable<Result> query(@NonNull Query<Result> query);
}

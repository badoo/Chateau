package com.badoo.chateau.core.usecases.messages;

import android.support.annotation.NonNull;

import com.badoo.barf.data.repo.Repository;
import com.badoo.barf.usecase.UseCase;
import com.badoo.chateau.core.model.Message;
import com.badoo.chateau.core.repos.messages.MessageDataSource.LoadResult;
import com.badoo.chateau.core.repos.messages.MessageQueries.LoadQuery;

import rx.Observable;

@UseCase
public class LoadMessages<M extends Message> {

    private final Repository<M> mRepo;

    public LoadMessages(Repository<M> repo) {
        mRepo = repo;
    }

    public Observable<LoadResult<M>> all(@NonNull String conversationId) {
        return mRepo.query(new LoadQuery<>(conversationId, LoadQuery.Type.ALL, null, null));
    }

    public Observable<LoadResult<M>> older(@NonNull String conversationId) {
        return mRepo.query(new LoadQuery<>(conversationId, LoadQuery.Type.OLDER, null, null));
    }

    public Observable<LoadResult<M>> newer(@NonNull String conversationId) {
        return mRepo.query(new LoadQuery<>(conversationId, LoadQuery.Type.NEWER, null, null));

    }
}

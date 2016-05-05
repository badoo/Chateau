package com.badoo.chateau.core.usecases.messages;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.badoo.barf.data.repo.Repository;
import com.badoo.barf.usecase.UseCase;
import com.badoo.chateau.core.model.Message;
import com.badoo.chateau.core.repos.messages.MessageQueries;

import rx.Observable;

@UseCase
public class LoadMessages<M extends Message> {

    private final Repository<M> mRepo;

    public LoadMessages(Repository<M> repo) {
        mRepo = repo;
    }

    public Observable<Boolean> execute(@NonNull String conversationId) {
        return execute(conversationId, null);
    }

    public Observable<Boolean> execute(@NonNull String conversationId, @Nullable M chunkBefore) {
        return mRepo.query(new MessageQueries.LoadMessagesQuery<>(conversationId, chunkBefore));
    }
}

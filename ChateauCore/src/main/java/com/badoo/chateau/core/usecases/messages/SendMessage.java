package com.badoo.chateau.core.usecases.messages;

import android.support.annotation.NonNull;

import com.badoo.barf.data.repo.Repository;
import com.badoo.barf.usecase.UseCase;
import com.badoo.chateau.core.model.Message;
import com.badoo.chateau.core.repos.messages.MessageQueries.SendQuery;

import rx.Observable;

/**
 * Use case for sending a new message
 */
@UseCase
public class SendMessage<M extends Message> {

    private final Repository<? extends Message> mRepo;

    public SendMessage(Repository<? extends Message> repo) {
        mRepo = repo;
    }

    public Observable<Void> execute(@NonNull String conversationId, @NonNull M message) {
        return mRepo.query(new SendQuery<>(conversationId, message))
            .ignoreElements()
            .cast(Void.class);
    }
}

package com.badoo.chateau.core.usecases.messages;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.badoo.barf.data.repo.Repository;
import com.badoo.barf.usecase.UseCase;
import com.badoo.chateau.core.model.Message;
import com.badoo.chateau.core.repos.messages.MessageQueries;

import rx.Observable;

/**
 * Use case for sending a new message
 */
@UseCase
public class SendMessage {

    private final Repository<? extends Message> mRepo;

    public SendMessage(Repository<? extends Message> repo) {
        mRepo = repo;
    }

    public Observable<Void> execute(@NonNull String conversationId, @Nullable String message, @Nullable Uri mediaUri) {
        return mRepo.query(new MessageQueries.SendMessageQuery(conversationId, message, mediaUri))
            .ignoreElements();
    }
}

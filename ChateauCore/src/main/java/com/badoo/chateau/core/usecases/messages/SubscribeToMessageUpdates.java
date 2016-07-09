package com.badoo.chateau.core.usecases.messages;

import android.support.annotation.NonNull;

import com.badoo.barf.data.repo.Repository;
import com.badoo.barf.usecase.UseCase;
import com.badoo.chateau.core.model.Message;
import com.badoo.chateau.core.repos.messages.MessageDataSource.Update;
import com.badoo.chateau.core.repos.messages.MessageQueries;

import rx.Observable;

/**
 * Use case for subscribing to new messages in a conversation
 */
@UseCase
public class SubscribeToMessageUpdates<M extends Message> {

    private final Repository<M> mRepo;

    public SubscribeToMessageUpdates(Repository<M> repo) {
        mRepo = repo;
    }

    public Observable<Update<M>> forConversation(@NonNull String conversationId) {
        return mRepo.query(new MessageQueries.SubscribeQuery<>(conversationId));
    }

    public Observable<Update<M>> all() {
        return mRepo.query(new MessageQueries.SubscribeQuery<>());
    }
}

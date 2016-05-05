package com.badoo.chateau.core.usecases.messages;

import android.support.annotation.NonNull;

import com.badoo.barf.data.repo.Repository;
import com.badoo.barf.usecase.UseCase;
import com.badoo.chateau.core.model.Message;
import com.badoo.chateau.core.repos.messages.MessageQueries;

import java.util.List;

import rx.Observable;

/**
 * Use case for subscribing to new messages in a conversation
 */
@UseCase
public class SubscribeToMessages<M extends Message> {

    private final Repository<M> mRepo;

    public SubscribeToMessages(Repository<M> repo) {
        mRepo = repo;
    }

    public Observable<List<M>> execute(@NonNull String conversationId) {
        return mRepo.query(new MessageQueries.SubscribeToMessagesQuery<>(conversationId));
    }
}

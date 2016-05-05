package com.badoo.chateau.core.usecases.conversations;

import android.support.annotation.NonNull;

import com.badoo.barf.data.repo.Repository;
import com.badoo.barf.usecase.UseCase;
import com.badoo.chateau.core.model.Conversation;
import com.badoo.chateau.core.repos.conversations.ConversationQueries;

import rx.Observable;

/**
 * Use case for marking a conversation as read
 */
@UseCase
public class MarkConversationRead {

    private final Repository<? extends Conversation> mConversationRepository;

    public MarkConversationRead(Repository<? extends Conversation> conversationRepository) {
        mConversationRepository = conversationRepository;
    }

    public Observable<Void> execute(@NonNull String conversationId) {
        return mConversationRepository.query(new ConversationQueries.MarkConversationReadQuery(conversationId));
    }
}


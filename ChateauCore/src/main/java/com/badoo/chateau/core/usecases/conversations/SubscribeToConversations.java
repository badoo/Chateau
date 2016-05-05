package com.badoo.chateau.core.usecases.conversations;

import com.badoo.barf.data.repo.Repository;
import com.badoo.barf.usecase.UseCase;
import com.badoo.chateau.core.model.Conversation;
import com.badoo.chateau.core.repos.conversations.ConversationQueries;

import java.util.List;

import rx.Observable;

/**
 * Use case for subscribing to conversation updates
 */
@UseCase
public class SubscribeToConversations<C extends Conversation> {

    private Repository<C> mConversationRepository;

    public SubscribeToConversations(Repository<C> conversationRepository) {
        mConversationRepository = conversationRepository;
    }

    public Observable<List<C>> execute() {
        return mConversationRepository.query(new ConversationQueries.SubscribeToConversations<>());
    }
}

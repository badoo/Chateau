package com.badoo.chateau.core.usecases.conversations;

import com.badoo.barf.data.repo.Repository;
import com.badoo.barf.usecase.UseCase;
import com.badoo.chateau.core.model.Conversation;
import com.badoo.chateau.core.repos.conversations.ConversationQueries;

import rx.Observable;

/**
 * Use case for retrieving all conversations that the current user is involved in.
 */
@UseCase
public class LoadMyConversations {

    private Repository<? extends Conversation> mConversationRepository;

    public LoadMyConversations(Repository<? extends Conversation> conversationRepository) {
        mConversationRepository = conversationRepository;
    }

    public Observable<Boolean> execute() {
        return mConversationRepository.query(ConversationQueries.LoadConversationsQuery.query());
    }
}

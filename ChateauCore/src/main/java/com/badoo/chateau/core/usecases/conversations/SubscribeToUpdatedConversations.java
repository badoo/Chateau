package com.badoo.chateau.core.usecases.conversations;

import android.support.annotation.VisibleForTesting;

import com.badoo.barf.usecase.RepoUseCase;
import com.badoo.barf.usecase.UseCase;
import com.badoo.chateau.core.model.Conversation;
import com.badoo.chateau.core.repos.conversations.ConversationQuery;
import com.badoo.chateau.core.repos.conversations.ConversationRepository;

import rx.Observable;

/**
 * Use case for subscribing to conversation updates
 */
public class SubscribeToUpdatedConversations extends RepoUseCase<UseCase.NoParams, Conversation, ConversationRepository> {

    public SubscribeToUpdatedConversations() {
        super(ConversationRepository.KEY);
    }

    @VisibleForTesting
    protected SubscribeToUpdatedConversations(ConversationRepository repository) {
        super(repository);
    }


    @Override
    protected Observable<Conversation> createObservable(NoParams params) {
        return getRepo().query(new ConversationQuery.SubscribeToConversationUpdatesQuery());
    }
}

package com.badoo.chateau.core.usecases.conversations;

import android.support.annotation.VisibleForTesting;

import com.badoo.barf.usecase.RepoUseCase;
import com.badoo.barf.usecase.UseCase;
import com.badoo.chateau.core.model.Conversation;
import com.badoo.chateau.core.repos.conversations.ConversationQuery;
import com.badoo.chateau.core.repos.conversations.ConversationRepository;

import java.util.List;

import rx.Observable;

/**
 * Use case for retrieving all conversations that the current user is involved in.
 */
public class GetMyConversations extends RepoUseCase<UseCase.NoParams, List<Conversation>, ConversationRepository> {

    public GetMyConversations() {
        super(ConversationRepository.KEY);
    }

    @VisibleForTesting
    protected GetMyConversations(ConversationRepository repository) {
        super(repository);
    }


    @Override
    protected Observable<List<Conversation>> createObservable(NoParams params) {
        return getRepo().query(new ConversationQuery.GetConversationsForCurrentUserQuery()).toList();
    }
}

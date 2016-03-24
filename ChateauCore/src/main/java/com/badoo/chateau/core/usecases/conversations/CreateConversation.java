package com.badoo.chateau.core.usecases.conversations;

import android.support.annotation.VisibleForTesting;

import com.badoo.barf.usecase.RepoUseCase;
import com.badoo.chateau.core.model.Conversation;
import com.badoo.chateau.core.model.User;
import com.badoo.chateau.core.repos.conversations.ConversationQuery;
import com.badoo.chateau.core.repos.conversations.ConversationRepository;

import rx.Observable;


public class CreateConversation extends RepoUseCase<User, Conversation, ConversationRepository> {
    public CreateConversation() {
        super(ConversationRepository.KEY);
    }

    @VisibleForTesting
    protected CreateConversation(ConversationRepository repository) {
        super(repository);
    }

    @Override
    protected Observable<Conversation> createObservable(User user) {
        return getRepo().query(new ConversationQuery.CreateConversationQuery(user));
    }
}


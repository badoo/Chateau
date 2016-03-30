package com.badoo.chateau.core.usecases.conversations;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.badoo.barf.usecase.RepoUseCase;
import com.badoo.chateau.core.model.Conversation;
import com.badoo.chateau.core.repos.conversations.ConversationQuery;
import com.badoo.chateau.core.repos.conversations.ConversationRepository;

import java.util.List;

import rx.Observable;


public class DeleteConversations extends RepoUseCase<DeleteConversations.DeleteConversationsParams, List<Conversation>, ConversationRepository> {

    public DeleteConversations() {
        super(ConversationRepository.KEY);
    }

    @VisibleForTesting
    protected DeleteConversations(ConversationRepository repository) {
        super(repository);
    }

    @Override
    protected Observable<List<Conversation>> createObservable(DeleteConversationsParams params) {
        return getRepo().query(new ConversationQuery.DeleteConversationsQuery(params.mConversations)).toList();
    }

    public static final class DeleteConversationsParams {

        @NonNull
        private final List<Conversation> mConversations;

        public DeleteConversationsParams(@NonNull List<Conversation> conversations) {
            mConversations = conversations;
        }
    }
}


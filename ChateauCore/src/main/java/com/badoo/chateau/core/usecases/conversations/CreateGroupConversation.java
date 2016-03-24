package com.badoo.chateau.core.usecases.conversations;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.badoo.barf.usecase.RepoUseCase;
import com.badoo.chateau.core.model.Conversation;
import com.badoo.chateau.core.model.User;
import com.badoo.chateau.core.repos.conversations.ConversationQuery;
import com.badoo.chateau.core.repos.conversations.ConversationRepository;

import java.util.List;

import rx.Observable;

public class CreateGroupConversation extends RepoUseCase<CreateGroupConversation.CreateGroupConversationParams, Conversation, ConversationRepository> {
    public CreateGroupConversation() {
        super(ConversationRepository.KEY);
    }

    @VisibleForTesting
    protected CreateGroupConversation(ConversationRepository repository) {
        super(repository);
    }


    @Override
    protected Observable<Conversation> createObservable(CreateGroupConversationParams params) {
        return getRepo().query(new ConversationQuery.CreateGroupConversationQuery(params.mUsers, params.mName));
    }

    public static final class CreateGroupConversationParams {
        @NonNull
        final List<User> mUsers;
        @NonNull
        final String mName;

        public CreateGroupConversationParams(@NonNull List<User> users, @NonNull String name) {
            mUsers = users;
            mName = name;
        }
    }
}


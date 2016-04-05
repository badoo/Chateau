package com.badoo.chateau.core.usecases.conversations;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.badoo.barf.usecase.RepoUseCase;
import com.badoo.chateau.core.model.Conversation;
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
        return getRepo().query(new ConversationQuery.CreateGroupConversationQuery(params.mUserIds, params.mName));
    }

    public static final class CreateGroupConversationParams {
        @NonNull
        final List<String> mUserIds;
        @NonNull
        final String mName;

        public CreateGroupConversationParams(@NonNull List<String> userIds, @NonNull String name) {
            mUserIds = userIds;
            mName = name;
        }
    }
}


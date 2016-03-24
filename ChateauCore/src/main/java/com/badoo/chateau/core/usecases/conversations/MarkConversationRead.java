package com.badoo.chateau.core.usecases.conversations;

import android.support.annotation.VisibleForTesting;

import com.badoo.barf.usecase.RepoUseCase;
import com.badoo.chateau.core.model.Conversation;
import com.badoo.chateau.core.repos.conversations.ConversationQuery;
import com.badoo.chateau.core.repos.conversations.ConversationRepository;
import com.badoo.chateau.core.usecases.messages.ChatParams;

import rx.Observable;


public class MarkConversationRead extends RepoUseCase<ChatParams, Conversation, ConversationRepository> {
    public MarkConversationRead() {
        super(ConversationRepository.KEY);
    }

    @VisibleForTesting
    protected MarkConversationRead(ConversationRepository repository) {
        super(repository);
    }


    @Override
    protected Observable<Conversation> createObservable(ChatParams params) {
        return getRepo().query(new ConversationQuery.MarkConversationReadQuery(params.mChatId));
    }
}


package com.badoo.chateau.core.usecases.conversations;

import android.support.annotation.VisibleForTesting;

import com.badoo.barf.usecase.RepoUseCase;
import com.badoo.chateau.core.model.Conversation;
import com.badoo.chateau.core.repos.conversations.ConversationQuery;
import com.badoo.chateau.core.repos.conversations.ConversationRepository;
import com.badoo.chateau.core.usecases.messages.ChatParams;

import rx.Observable;

/**
 * Use case for retrieving data for a single conversation (not including the actual messages in it)
 */
public class GetConversation extends RepoUseCase<ChatParams, Conversation, ConversationRepository> {

    public GetConversation() {
        super(ConversationRepository.KEY);
    }

    @VisibleForTesting
    protected GetConversation(ConversationRepository repository) {
        super(repository);
    }


    @Override
    protected Observable<Conversation> createObservable(ChatParams params) {
        return getRepo().query(new ConversationQuery.GetConversationQuery(params.mChatId));
    }
}

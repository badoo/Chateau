package com.badoo.chateau.core.usecases.conversations;

import android.support.annotation.NonNull;

import com.badoo.barf.data.repo.Repository;
import com.badoo.barf.usecase.UseCase;
import com.badoo.chateau.core.model.Conversation;
import com.badoo.chateau.core.repos.conversations.ConversationQueries;

import rx.Observable;

/**
 * Use case for retrieving data for a single conversation (not including the actual messages in it)
 */
@UseCase
public class GetConversation<C extends Conversation> {

    private final Repository<C> mConversationRepository;

    public GetConversation(Repository<C> conversationRepository) {
        mConversationRepository = conversationRepository;
    }

    public Observable<C> execute(@NonNull String conversationId) {
        return  mConversationRepository.query(new ConversationQueries.GetConversationQuery<>(conversationId));
    }
}

package com.badoo.chateau.core.usecases.conversations;

import android.support.annotation.NonNull;

import com.badoo.barf.data.repo.Repository;
import com.badoo.barf.usecase.UseCase;
import com.badoo.chateau.core.model.Conversation;
import com.badoo.chateau.core.repos.conversations.ConversationQueries;

import rx.Observable;

/**
 * User case for creating a conversation with a single user. To create a conversation with multiple users (group chat) use {@link CreateGroupConversation}
 */
@UseCase
public class CreateConversation<C extends Conversation> {

    private final Repository<C> mConversationRepository;

    public CreateConversation(Repository<C> conversationRepository) {
        mConversationRepository = conversationRepository;
    }

    public Observable<C> execute(@NonNull String userId) {
        return mConversationRepository.query(new ConversationQueries.CreateConversationQuery<>(userId));
    }
}


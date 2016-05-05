package com.badoo.chateau.core.usecases.conversations;

import android.support.annotation.NonNull;

import com.badoo.barf.data.repo.Repository;
import com.badoo.barf.usecase.UseCase;
import com.badoo.chateau.core.model.Conversation;
import com.badoo.chateau.core.repos.conversations.ConversationQueries;

import java.util.List;

import rx.Observable;

/**
 * User case for creating a conversation with multiple users (group chat). To create a conversation with a single user (group chat) use {@link CreateConversation}
 */
@UseCase
public class CreateGroupConversation<C extends Conversation> {

    private final Repository<C> mConversationRepository;

    public CreateGroupConversation(Repository<C> conversationRepository) {
        mConversationRepository = conversationRepository;
    }

    public Observable<C> execute(@NonNull List<String> userIds, @NonNull String name) {
        return mConversationRepository.query(new ConversationQueries.CreateGroupConversationQuery<>(userIds, name));
    }
}


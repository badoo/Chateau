package com.badoo.chateau.core.usecases.conversations;

import android.support.annotation.NonNull;

import com.badoo.barf.data.repo.Repository;
import com.badoo.barf.usecase.UseCase;
import com.badoo.chateau.core.model.Conversation;
import com.badoo.chateau.core.repos.conversations.ConversationQueries;

import java.util.List;

import rx.Observable;

/**
 * User case for deleting an existing conversation (either with a single user or a group)
 */
@UseCase
public class DeleteConversations {

    private final Repository<? extends Conversation> mConversationRepository;

    public DeleteConversations(Repository<? extends Conversation> conversationRepository) {
        mConversationRepository = conversationRepository;
    }

    public Observable<Void> execute(@NonNull List<Conversation> conversations) {
        return mConversationRepository.query(new ConversationQueries.DeleteConversationsQuery<>(conversations));
    }
}


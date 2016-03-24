package com.badoo.chateau.core.repos.conversations;

import android.support.annotation.NonNull;

import com.badoo.barf.data.repo.annotations.Handles;
import com.badoo.chateau.core.model.Conversation;
import com.badoo.chateau.core.repos.conversations.ConversationQuery.CreateConversationQuery;
import com.badoo.chateau.core.repos.conversations.ConversationQuery.CreateGroupConversationQuery;
import com.badoo.chateau.core.repos.conversations.ConversationQuery.DeleteConversationsQuery;
import com.badoo.chateau.core.repos.conversations.ConversationQuery.GetConversationQuery;
import com.badoo.chateau.core.repos.conversations.ConversationQuery.GetConversationsForCurrentUserQuery;
import com.badoo.chateau.core.repos.conversations.ConversationQuery.MarkConversationReadQuery;
import com.badoo.chateau.core.repos.conversations.ConversationQuery.SubscribeToConversationUpdatesQuery;

import rx.Observable;

/**
 * Defines a data source providing conversations the {@link ConversationsRepository}
 */
public interface ConversationDataSource {

    /**
     * Return a list containing all the logged in users conversations.
     */
    @Handles(GetConversationsForCurrentUserQuery.class)
    @NonNull
    Observable<Conversation> getConversationsForLoggedInUser(GetConversationsForCurrentUserQuery query);

    /**
     * Return a single conversation
     */
    @Handles(GetConversationQuery.class)
    @NonNull
    Observable<Conversation> getConversation(GetConversationQuery query);

    /**
     * Returns updated conversations
     */
    @Handles(SubscribeToConversationUpdatesQuery.class)
    @NonNull
    Observable<Conversation> subscribeToUpdates(SubscribeToConversationUpdatesQuery query);

    /**
     * Creates a conversation for a given list of users
     */
    @Handles(CreateGroupConversationQuery.class)
    @NonNull
    Observable<Conversation> createGroupConversation(CreateGroupConversationQuery query);

    /**
     * Creates a conversation for a given list of users
     */
    @Handles(CreateConversationQuery.class)
    @NonNull
    Observable<Conversation> createConversation(CreateConversationQuery query);

    /**
     * Marks a conversation as read up to the latest message
     */
    @Handles(MarkConversationReadQuery.class)
    @NonNull
    Observable<Void> markConversationRead(MarkConversationReadQuery query);

    /**
     * Marks a conversation as read up to the latest message
     */
    @Handles(DeleteConversationsQuery.class)
    @NonNull
    Observable<Conversation> deleteConversations(DeleteConversationsQuery query);
}

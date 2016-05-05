package com.badoo.chateau.core.repos.conversations;

import android.support.annotation.NonNull;

import com.badoo.barf.data.repo.annotations.Handles;
import com.badoo.chateau.core.model.Conversation;
import com.badoo.chateau.core.repos.conversations.ConversationQueries.CreateConversationQuery;
import com.badoo.chateau.core.repos.conversations.ConversationQueries.CreateGroupConversationQuery;
import com.badoo.chateau.core.repos.conversations.ConversationQueries.DeleteConversationsQuery;
import com.badoo.chateau.core.repos.conversations.ConversationQueries.LoadConversationsQuery;
import com.badoo.chateau.core.repos.conversations.ConversationQueries.MarkConversationReadQuery;
import com.badoo.chateau.core.repos.conversations.ConversationQueries.SubscribeToConversations;

import java.util.List;

import rx.Observable;

/**
 * Defines a data source providing conversations
 */
public interface ConversationDataSource<C extends Conversation> {

    /**
     * Loads conversations and publishes updates via {@link #subscribeToConversations(SubscribeToConversations)}.  The returned observable
     * contains a single boolean indicating if it is possible to get more conversations in this direction.  For example if a request was made
     * with chunkBefore, <code>true</code> would be published if requesting using chunkBefore for the oldest conversation could return more
     * data.  <code>true</code> will always be returned if chunkBefore and chunkAfter are <code>null</code>
     */
    @Handles(LoadConversationsQuery.class)
    @NonNull
    Observable<Boolean> loadConversations(LoadConversationsQuery<C> query);

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
    Observable<Void> deleteConversations(DeleteConversationsQuery<C> query);

    /**
     * Used to listen to conversations stored in the data source.
     */
    @Handles(SubscribeToConversations.class)
    @NonNull
    Observable<List<C>> subscribeToConversations(SubscribeToConversations<C> query);

    /**
     * Returns an single conversation matching the query if it exists.
     */
    @Handles(ConversationQueries.GetConversationQuery.class)
    @NonNull
    Observable<C> getConversation(ConversationQueries.GetConversationQuery query);

    /**
     * Creates a conversation for a given list of users
     */
    @Handles(CreateConversationQuery.class)
    @NonNull
    Observable<C> createConversation(CreateConversationQuery<C> query);

    /**
     * Creates a conversation for a given list of users
     */
    @Handles(CreateGroupConversationQuery.class)
    @NonNull
    Observable<C> createGroupConversation(CreateGroupConversationQuery<C> query);

}

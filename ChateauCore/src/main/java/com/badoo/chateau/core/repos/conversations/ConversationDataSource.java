package com.badoo.chateau.core.repos.conversations;

import android.support.annotation.NonNull;

import com.badoo.barf.data.repo.annotations.Handles;
import com.badoo.chateau.core.model.Conversation;
import com.badoo.chateau.core.repos.conversations.ConversationQueries.CreateConversationQuery;
import com.badoo.chateau.core.repos.conversations.ConversationQueries.CreateGroupConversationQuery;
import com.badoo.chateau.core.repos.conversations.ConversationQueries.DeleteConversationsQuery;
import com.badoo.chateau.core.repos.conversations.ConversationQueries.GetConversationQuery;
import com.badoo.chateau.core.repos.conversations.ConversationQueries.LoadConversationsQuery;
import com.badoo.chateau.core.repos.conversations.ConversationQueries.MarkConversationReadQuery;
import com.badoo.chateau.core.repos.conversations.ConversationQueries.SubscribeToUpdatesQuery;

import java.util.List;

import rx.Observable;

/**
 * Defines a data source providing conversations
 */
public interface ConversationDataSource<C extends Conversation> {

    /**
     * Class used to indicate what can be subsequently loaded after a call to {@link #load(LoadConversationsQuery)}
     */
    class LoadResult<C extends Conversation> {

        private final List<C> mConversations;
        private final boolean mCanMoveBackwards;
        private final boolean mCanMoveForwards;

        public LoadResult(@NonNull List<C> conversations, boolean canMoveBackwards, boolean canMoveForwards) {
            mCanMoveBackwards = canMoveBackwards;
            mCanMoveForwards = canMoveForwards;
            mConversations = conversations;
        }

        public List<C> getConversations() {
            return mConversations;
        }

        public boolean canMoveBackwards() {
            return mCanMoveBackwards;
        }

        public boolean canMoveForwards() {
            return mCanMoveForwards;
        }

        @Override
        public String toString() {
            return "LoadResult{" +
                "mConversations=" + mConversations.size() +
                ", mCanMoveBackwards=" + mCanMoveBackwards +
                ", mCanMoveForwards=" + mCanMoveForwards +
                '}';
        }

    }

    /**
     * Loads conversations and publishes updates via {@link #subscribe(SubscribeToUpdatesQuery)}.  The returned observable
     * contains a single boolean indicating if it is possible to get more conversations in this direction.  For example if a request was made
     * with chunkBefore, <code>true</code> would be published if requesting using chunkBefore for the oldest conversation could return more
     * data.  <code>true</code> will always be returned if chunkBefore and chunkAfter are <code>null</code>
     */
    @Handles(LoadConversationsQuery.class)
    @NonNull
    Observable<LoadResult<C>> load(LoadConversationsQuery<C> query);

    /**
     * Marks a conversation as read up to the latest message
     */
    @Handles(MarkConversationReadQuery.class)
    @NonNull
    Observable<Void> markRead(MarkConversationReadQuery query);

    /**
     * Marks a conversation as read up to the latest message
     */
    @Handles(DeleteConversationsQuery.class)
    @NonNull
    Observable<List<C>> delete(DeleteConversationsQuery<C> query);

    /**
     * Notifies when new data is available to be loaded
     */
    @Handles(SubscribeToUpdatesQuery.class)
    @NonNull
    Observable<Boolean> subscribe(SubscribeToUpdatesQuery query);

    /**
     * Returns an single conversation matching the query if it exists.
     */
    @Handles(GetConversationQuery.class)
    @NonNull
    Observable<C> get(GetConversationQuery query);

    //TODO Move to example app ext. data source
    /**
     * Creates a conversation for a given list of users
     */
    @Handles(CreateConversationQuery.class)
    @NonNull
    Observable<C> create(CreateConversationQuery<C> query);

    //TODO Move to example app ext. data source
    /**
     * Creates a conversation for a given list of users
     */
    @Handles(CreateGroupConversationQuery.class)
    @NonNull
    Observable<C> create(CreateGroupConversationQuery<C> query);

}

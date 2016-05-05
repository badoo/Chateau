package com.badoo.chateau.core.repos.conversations;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.badoo.barf.data.repo.Query;
import com.badoo.chateau.core.model.Conversation;

import java.util.List;

/**
 * Query class for performing operations on the ConversationRepository
 */
@SuppressWarnings("unused")
public abstract class ConversationQueries {

    /**
     * Query that returns all the conversations of the current user
     */
    public static class LoadConversationsQuery<C extends Conversation> implements Query<Boolean> {

        public static <C extends Conversation> LoadConversationsQuery<C> query() {
            return new LoadConversationsQuery<>();
        }

        public static <C extends Conversation> LoadConversationsQuery<C> queryBefore(C chunkBefore) {
            return new LoadConversationsQuery<C>().setChunkBefore(chunkBefore);
        }

        public static <C extends Conversation> LoadConversationsQuery<C> queryAfter(C chunkAfter) {
            return new LoadConversationsQuery<C>().setChunkAfter(chunkAfter);
        }

        @Nullable
        private C mChunkBefore;
        @Nullable
        private C mChunkAfter;

        private LoadConversationsQuery() { }

        private LoadConversationsQuery<C> setChunkBefore(@Nullable C chunkBefore) {
            mChunkBefore = chunkBefore;
            return this;
        }

        @Nullable
        public C getChunkBefore() {
            return mChunkBefore;
        }

        private LoadConversationsQuery<C> setChunkAfter(@Nullable C chunkAfter) {
            mChunkAfter = chunkAfter;
            return this;
        }

        @Nullable
        public C getChunkAfter() {
            return mChunkAfter;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof LoadConversationsQuery)) return false;

            LoadConversationsQuery<?> that = (LoadConversationsQuery<?>) o;

            if (mChunkBefore != null ? !mChunkBefore.equals(that.mChunkBefore) : that.mChunkBefore != null) return false;
            return mChunkAfter != null ? mChunkAfter.equals(that.mChunkAfter) : that.mChunkAfter == null;

        }

        @Override
        public int hashCode() {
            int result = mChunkBefore != null ? mChunkBefore.hashCode() : 0;
            result = 31 * result + (mChunkAfter != null ? mChunkAfter.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "GetConversationsQuery{" +
                "mChunkBefore=" + mChunkBefore +
                ", mChunkAfter=" + mChunkAfter +
                '}';
        }
    }

    /**
     * Query that returns an Observable that will receive updates whenever a conversation is updated
     */
    public static class SubscribeToConversations<C extends Conversation> implements Query<List<C>> {

        @Override
        public boolean equals(Object o) {
            return o != null && o instanceof SubscribeToConversations;
        }

        @Override
        public int hashCode() {
            return 31;
        }

        @Override
        public String toString() {
            return "SubscribeToConversationUpdatesQuery{}";
        }
    }

    /**
     * Query that creates a new conversation with a single user
     */
    public static class CreateConversationQuery<C extends Conversation> implements Query<C> {

        @NonNull
        private final String mUserId;

        public CreateConversationQuery(@NonNull String userId) {
            mUserId = userId;
        }

        @NonNull
        public String getUserId() {
            return mUserId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CreateConversationQuery that = (CreateConversationQuery) o;

            return mUserId.equals(that.mUserId);

        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + mUserId.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "CreateConversationQuery{" +
                "mUser=" + mUserId +
                '}';
        }
    }

    /**
     * Query that creates a new group conversation
     */
    public static class CreateGroupConversationQuery<C extends Conversation> implements Query<C> {

        @NonNull
        private final String mName;
        @NonNull
        private final List<String> mUserIds;

        public CreateGroupConversationQuery(@NonNull List<String> userIds, @NonNull String name) {
            mUserIds = userIds;
            mName = name;
        }

        @NonNull
        public String getName() {
            return mName;
        }

        @NonNull
        public List<String> getUserIds() {
            return mUserIds;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CreateGroupConversationQuery that = (CreateGroupConversationQuery) o;

            if (!mName.equals(that.mName)) return false;
            return mUserIds.equals(that.mUserIds);

        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + mName.hashCode();
            result = 31 * result + mUserIds.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "CreateGroupConversationQuery{" +
                "mName='" + mName + '\'' +
                ", mUserIds=" + mUserIds +
                '}';
        }
    }

    /**
     * Query that creates a new group conversation
     */
    public static class MarkConversationReadQuery implements Query<Void> {

        @NonNull
        private final String mConversationId;

        public MarkConversationReadQuery(@NonNull String conversationId) {
            mConversationId = conversationId;
        }

        @NonNull
        public String getConversationId() {
            return mConversationId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MarkConversationReadQuery that = (MarkConversationReadQuery) o;

            return mConversationId.equals(that.mConversationId);

        }

        @Override
        public int hashCode() {
            return mConversationId.hashCode();
        }

        @Override
        public String toString() {
            return "MarkConversationReadQuery{" +
                "mConversationId='" + mConversationId + '\'' +
                '}';
        }
    }

    /**
     * Query that deletes one or more conversations
     */
    public static class DeleteConversationsQuery<C extends Conversation> implements Query<Void> {

        @NonNull
        private final List<C> mConversations;

        public DeleteConversationsQuery(@NonNull List<C> conversations) {
            mConversations = conversations;
        }

        @NonNull
        public List<C> getConversations() {
            return mConversations;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DeleteConversationsQuery that = (DeleteConversationsQuery) o;

            return mConversations.equals(that.mConversations);

        }

        @Override
        public int hashCode() {
            return mConversations.hashCode();
        }

        @Override
        public String toString() {
            return "DeleteConversationsQuery{" +
                "mConversations=" + mConversations +
                '}';
        }
    }

    /**
     * Query that returns a single conversation
     */
    public static class GetConversationQuery<C extends Conversation> implements Query<C> {

        @NonNull
        private final String mConversationId;

        public GetConversationQuery(@NonNull String conversationId) {
            mConversationId = conversationId;
        }

        @NonNull
        public String getConversationId() {
            return mConversationId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            GetConversationQuery that = (GetConversationQuery) o;

            return mConversationId.equals(that.mConversationId);

        }

        @Override
        public int hashCode() {
            return mConversationId.hashCode();
        }

        @Override
        public String toString() {
            return "GetConversationQuery{" +
                "mConversationId='" + mConversationId + '\'' +
                '}';
        }
    }

}
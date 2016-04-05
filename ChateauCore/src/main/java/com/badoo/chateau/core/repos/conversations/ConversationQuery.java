package com.badoo.chateau.core.repos.conversations;

import android.support.annotation.NonNull;

import com.badoo.barf.data.repo.Query;
import com.badoo.chateau.core.model.Conversation;
import com.badoo.chateau.core.model.User;

import java.util.List;

public abstract class ConversationQuery implements Query {

    public static class GetConversationsForCurrentUserQuery extends ConversationQuery {

        @Override
        public boolean equals(Object o) {
            return o != null && o instanceof GetConversationsForCurrentUserQuery;
        }

        @Override
        public int hashCode() {
            return 31;
        }

        @Override
        public String toString() {
            return "GetConversationsForCurrentUserQuery{}";
        }
    }

    public static class SubscribeToConversationUpdatesQuery extends ConversationQuery {

        @Override
        public boolean equals(Object o) {
            return o != null && o instanceof SubscribeToConversationUpdatesQuery;
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

    public static class CreateConversationQuery extends ConversationQuery {

        @NonNull
        private final User mUser;

        public CreateConversationQuery(@NonNull User user) {
            mUser = user;
        }

        @NonNull
        public User getUser() {
            return mUser;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CreateConversationQuery that = (CreateConversationQuery) o;

            return mUser.equals(that.mUser);

        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + mUser.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "CreateConversationQuery{" +
                "mUser=" + mUser +
                '}';
        }
    }

    public static class CreateGroupConversationQuery extends ConversationQuery {

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

    public static class MarkConversationReadQuery extends ConversationQuery {

        @NonNull
        private final String mChatId;

        public MarkConversationReadQuery(@NonNull String chatId) {
            mChatId = chatId;
        }

        @NonNull
        public String getChatId() {
            return mChatId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MarkConversationReadQuery that = (MarkConversationReadQuery) o;

            return mChatId.equals(that.mChatId);

        }

        @Override
        public int hashCode() {
            return mChatId.hashCode();
        }

        @Override
        public String toString() {
            return "MarkConversationReadQuery{" +
                "mChatId='" + mChatId + '\'' +
                '}';
        }
    }

    public static class DeleteConversationsQuery extends ConversationQuery {

        @NonNull
        private final List<Conversation> mConversations;

        public DeleteConversationsQuery(@NonNull List<Conversation> conversations) {
            mConversations = conversations;
        }

        @NonNull
        public List<Conversation> getConversations() {
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

    public static class GetConversationQuery extends ConversationQuery {

        @NonNull
        private final String mChatId;

        public GetConversationQuery(@NonNull String chatId) {
            mChatId = chatId;
        }

        @NonNull
        public String getChatId() {
            return mChatId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            GetConversationQuery that = (GetConversationQuery) o;

            return mChatId.equals(that.mChatId);

        }

        @Override
        public int hashCode() {
            return mChatId.hashCode();
        }

        @Override
        public String toString() {
            return "GetConversationQuery{" +
                "mChatId='" + mChatId + '\'' +
                '}';
        }
    }

}
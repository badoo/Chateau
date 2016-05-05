package com.badoo.chateau.core.repos.istyping;

import android.support.annotation.NonNull;

import com.badoo.barf.data.repo.Query;
import com.badoo.chateau.core.model.User;

/**
 * Query class for performing operations on the IsTypingRepository
 */
public abstract class IsTypingQueries {

    /**
     * Query for notifying that the current user is typing
     */
    public static class SendIsTyping implements Query<Void> {

        private final String mConversationId;

        public SendIsTyping(@NonNull String conversationId) {
            mConversationId = conversationId;
        }

        public String getConversationId() {
            return mConversationId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SendIsTyping that = (SendIsTyping) o;

            return mConversationId.equals(that.mConversationId);

        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + mConversationId.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "SendIsTyping{" +
                "mConversationId='" + mConversationId + '\'' +
                '}';
        }
    }

    /**
     * Query for subscribing to updates when other users are typing
     */
    public static class SubscribeToUsersTypingQuery<U extends User> implements Query<U> {

        private final String mConversationId;

        public SubscribeToUsersTypingQuery(@NonNull String conversationId) {
            mConversationId = conversationId;
        }

        public String getConversationId() {
            return mConversationId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SubscribeToUsersTypingQuery that = (SubscribeToUsersTypingQuery) o;

            return mConversationId.equals(that.mConversationId);

        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + mConversationId.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "SubscribeToUsersTyping{" +
                "mConversationId='" + mConversationId + '\'' +
                '}';
        }
    }
}

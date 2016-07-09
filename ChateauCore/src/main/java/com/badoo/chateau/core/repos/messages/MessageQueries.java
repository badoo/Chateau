package com.badoo.chateau.core.repos.messages;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.badoo.barf.data.repo.Query;
import com.badoo.chateau.core.model.Message;
import com.badoo.chateau.core.repos.messages.MessageDataSource.LoadResult;
import com.badoo.chateau.core.repos.messages.MessageDataSource.Update;

import java.util.List;

/**
 * Query class for performing operations on the MessageRepository
 */
public abstract class MessageQueries {

    /**
     * Query for retrieving messages in a conversation with optional paging.
     */
    public static class LoadQuery<M extends Message> implements Query<LoadResult<M>> {

        public enum Type {
            ALL,
            NEWER,
            OLDER
        }

        @NonNull
        private final String mConversationId;
        @NonNull
        private final Type mType;
        @Nullable
        private final M mOldest;
        @Nullable
        private final M mNewest;

        public LoadQuery(@NonNull String conversationId, @NonNull Type type, @Nullable M oldest, @Nullable M newest) {
            mConversationId = conversationId;
            mType = type;
            mOldest = oldest;
            mNewest = newest;
        }

        @NonNull
        public String getConversationId() {
            return mConversationId;
        }

        @NonNull
        public Type getType() {
            return mType;
        }

        @Nullable
        public M getOldest() {
            return mOldest;
        }

        @Nullable
        public M getNewest() {
            return mNewest;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            LoadQuery<?> that = (LoadQuery<?>) o;

            if (!mConversationId.equals(that.mConversationId)) return false;
            if (mType != that.mType) return false;
            if (mOldest != null ? !mOldest.equals(that.mOldest) : that.mOldest != null) return false;
            return mNewest != null ? mNewest.equals(that.mNewest) : that.mNewest == null;

        }

        @Override
        public int hashCode() {
            int result = mConversationId.hashCode();
            result = 31 * result + mType.hashCode();
            result = 31 * result + (mOldest != null ? mOldest.hashCode() : 0);
            result = 31 * result + (mNewest != null ? mNewest.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "LoadQuery{" +
                "mConversationId='" + mConversationId + '\'' +
                ", mType=" + mType +
                ", mOldest=" + mOldest +
                ", mNewest=" + mNewest +
                '}';
        }
    }

    /**
     * Query for retrieving a list of messages that have not been delivered due to errors.
     */
    public static class GetUndeliveredQuery<M extends Message> implements Query<List<M>> {

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            return (o == null || getClass() == o.getClass());
        }

        @Override
        public int hashCode() {
            return 42;
        }

        @Override
        public String toString() {
            return "GetUndeliveredQuery{}";
        }
    }

    /**
     * Query for subscribing to new messages in a conversation, or messages in all conversations
     */
    public static class SubscribeQuery<M extends Message> implements Query<Update<M>> {

        private final String mConversationId;

        public SubscribeQuery() {
            mConversationId = null;
        }

        public SubscribeQuery(@Nullable String conversationId) {
            mConversationId = conversationId;
        }

        public String getConversationId() {
            return mConversationId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SubscribeQuery)) return false;

            SubscribeQuery that = (SubscribeQuery) o;

            return mConversationId != null ? mConversationId.equals(that.mConversationId) : that.mConversationId == null;

        }

        @Override
        public int hashCode() {
            return mConversationId != null ? mConversationId.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "SubscribeQuery{" +
                "mConversationId='" + mConversationId + '\'' +
                '}';
        }
    }

    /**
     * Query for sending a new message
     */
    public static class SendQuery<M extends Message> implements Query<Void> {

        private final String mConversationId;
        private final M mMessage;

        public SendQuery(@NonNull String conversationId, @NonNull M message) {
            mConversationId = conversationId;
            mMessage = message;
        }

        @NonNull
        public String getConversationId() {
            return mConversationId;
        }

        public M getMessage() {
            return mMessage;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SendQuery<?> that = (SendQuery<?>) o;

            if (!mConversationId.equals(that.mConversationId)) return false;
            return mMessage.equals(that.mMessage);

        }

        @Override
        public int hashCode() {
            int result = mConversationId.hashCode();
            result = 31 * result + mMessage.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "SendQuery{" +
                "mConversationId='" + mConversationId + '\'' +
                ", mMessage=" + mMessage +
                '}';
        }
    }

}

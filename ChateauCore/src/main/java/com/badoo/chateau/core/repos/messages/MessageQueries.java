package com.badoo.chateau.core.repos.messages;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.badoo.barf.data.repo.Query;
import com.badoo.chateau.core.model.Message;

import java.util.List;

/**
 * Query class for performing operations on the MessageRepository
 */
public abstract class MessageQueries {

    /**
     * Query for retrieving messages in a conversation with optional paging.
     */
    public static class LoadMessagesQuery<M extends Message> implements Query<Boolean> {

        private final String mConversationId;
        @Nullable
        private final M mChunkBefore;

        /**
         * @param conversationId id of the conversation to load messages for
         * @param chunkBefore    if not null, load a chunk/page of messages that come before this one
         */
        public LoadMessagesQuery(@NonNull String conversationId, @Nullable M chunkBefore) {
            mConversationId = conversationId;
            mChunkBefore = chunkBefore;
        }

        public String getConversationId() {
            return mConversationId;
        }

        @Nullable
        public M getChunkBefore() {
            return mChunkBefore;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final LoadMessagesQuery that = (LoadMessagesQuery) o;

            return mConversationId.equals(that.mConversationId) && (mChunkBefore != null ? mChunkBefore.equals(that.mChunkBefore) : that.mChunkBefore == null);

        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (mConversationId.hashCode());
            result = 31 * result + (mChunkBefore != null ? mChunkBefore.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "LoadMessagesQuery{" +
                "mConversationId='" + mConversationId + '\'' +
                ", mChunkBefore=" + mChunkBefore +
                '}';
        }
    }

    /**
     * Query for subscribing to new messages in a conversation
     */
    public static class SubscribeToMessagesQuery<M extends Message> implements Query<List<M>> {

        private final String mConversationId;

        public SubscribeToMessagesQuery(@NonNull String conversationId) {
            mConversationId = conversationId;
        }

        public String getConversationId() {
            return mConversationId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SubscribeToMessagesQuery that = (SubscribeToMessagesQuery) o;

            return mConversationId.equals(that.mConversationId);

        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (mConversationId.hashCode());
            return result;
        }

        @Override
        public String toString() {
            return "SubscribeToMessagesQuery{" +
                "mConversationId='" + mConversationId + '\'' +
                '}';
        }
    }

    /**
     * Query for sending a new message
     */
    public static class SendMessageQuery implements Query<Void> {

        private final String mConversationId;
        private final String mMessage;
        private final Uri mMediaUri;

        public SendMessageQuery(@NonNull String conversationId, @Nullable String message, @Nullable Uri mediaUri ) {
            mConversationId = conversationId;
            mMessage = message;
            mMediaUri = mediaUri;
        }

        @NonNull
        public String getConversationId() {
            return mConversationId;
        }

        public String getMessage() {
            return mMessage;
        }

        public Uri getMediaUri() {
            return mMediaUri;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SendMessageQuery that = (SendMessageQuery) o;

            if (!mConversationId.equals(that.mConversationId)) return false;
            if (mMessage != null ? !mMessage.equals(that.mMessage) : that.mMessage != null) return false;
            return mMediaUri != null ? mMediaUri.equals(that.mMediaUri) : that.mMediaUri == null;

        }

        @Override
        public int hashCode() {
            int result = mConversationId.hashCode();
            result = 31 * result + (mMessage != null ? mMessage.hashCode() : 0);
            result = 31 * result + (mMediaUri != null ? mMediaUri.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "SendMessage{" +
                "mConversationId='" + mConversationId + '\'' +
                ", mMessage='" + mMessage + '\'' +
                ", mMediaUri=" + mMediaUri +
                '}';
        }
    }

}

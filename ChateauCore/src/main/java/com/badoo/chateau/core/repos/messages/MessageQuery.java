package com.badoo.chateau.core.repos.messages;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.badoo.barf.data.repo.Query;
import com.badoo.chateau.core.model.Message;

public abstract class MessageQuery implements Query {

    public static class GetMessages extends MessageQuery {

        private final String mChatId;
        @Nullable
        private final Message mChunkBefore;

        public GetMessages(@NonNull String chatId, @Nullable Message chunkBefore) {
            mChatId = chatId;
            mChunkBefore = chunkBefore;
        }

        public String getChatId() {
            return mChatId;
        }

        @Nullable
        public Message getChunkBefore() {
            return mChunkBefore;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final GetMessages that = (GetMessages) o;

            return mChatId.equals(that.mChatId) && (mChunkBefore != null ? mChunkBefore.equals(that.mChunkBefore) : that.mChunkBefore == null);

        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (mChatId.hashCode());
            result = 31 * result + (mChunkBefore != null ? mChunkBefore.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "GetMessages{" +
                "mChatId='" + mChatId + '\'' +
                ", mChunkBefore=" + mChunkBefore +
                '}';
        }
    }

    public static class SubscribeToNewMessagesForConversation extends MessageQuery {

        private final String mChatId;

        public SubscribeToNewMessagesForConversation(@NonNull String chatId) {
            mChatId = chatId;
        }

        public String getChatId() {
            return mChatId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SubscribeToNewMessagesForConversation that = (SubscribeToNewMessagesForConversation) o;

            return mChatId.equals(that.mChatId);

        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (mChatId.hashCode());
            return result;
        }

        @Override
        public String toString() {
            return "SubscribeToNewMessagesForConversation{" +
                "mChatId='" + mChatId + '\'' +
                '}';
        }
    }

    public static class GetUpdatedMessagesForConversation extends MessageQuery {

        private final String mChatId;

        public GetUpdatedMessagesForConversation(@NonNull String chatId) {
            mChatId = chatId;
        }

        public String getChatId() {
            return mChatId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            GetUpdatedMessagesForConversation that = (GetUpdatedMessagesForConversation) o;

            return mChatId.equals(that.mChatId);

        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (mChatId.hashCode());
            return result;
        }

        @Override
        public String toString() {
            return "GetUpdatedMessagesForConversation{" +
                "mChatId='" + mChatId + '\'' +
                '}';
        }
    }

    public static class SendMessage extends MessageQuery {

        private final String mChatId;
        private final Message mMessage;

        public SendMessage(@NonNull String chatId, @NonNull Message message) {
            mChatId = chatId;
            mMessage = message;
        }

        public Message getMessage() {
            return mMessage;
        }

        public String getChatId() {
            return mChatId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SendMessage that = (SendMessage) o;

            return mChatId.equals(that.mChatId) && mMessage.equals(that.mMessage);

        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (mChatId.hashCode());
            result = 31 * result + (mMessage.hashCode());
            return result;
        }

        @Override
        public String toString() {
            return "SendMessage{" +
                "mChatId='" + mChatId + '\'' +
                ", mMessage=" + mMessage +
                '}';
        }
    }

    public static class UserIsTypingQuery extends MessageQuery {

        private final String mChatId;

        public UserIsTypingQuery(@NonNull String chatId) {
            mChatId = chatId;
        }

        public String getChatId() {
            return mChatId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            UserIsTypingQuery that = (UserIsTypingQuery) o;

            return mChatId != null ? mChatId.equals(that.mChatId) : that.mChatId == null;

        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (mChatId != null ? mChatId.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "UserIsTypingQuery{" +
                "mChatId='" + mChatId + '\'' +
                '}';
        }
    }
}

package com.badoo.chateau.core.repos.istyping;

import android.support.annotation.NonNull;

import com.badoo.barf.data.repo.Query;

public abstract class IsTypingQuery implements Query {

    public static class SendIsTyping extends IsTypingQuery {

        private final String mChatId;

        public SendIsTyping(@NonNull String chatId) {
            mChatId = chatId;
        }

        public String getChatId() {
            return mChatId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SendIsTyping that = (SendIsTyping) o;

            return mChatId.equals(that.mChatId);

        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + mChatId.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "SendIsTyping{" +
                "mChatId='" + mChatId + '\'' +
                '}';
        }
    }

    public static class SubscribeToUsersTyping extends IsTypingQuery {

        private final String mChatId;

        public SubscribeToUsersTyping(@NonNull String chatId) {
            mChatId = chatId;
        }

        public String getChatId() {
            return mChatId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SubscribeToUsersTyping that = (SubscribeToUsersTyping) o;

            return mChatId.equals(that.mChatId);

        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + mChatId.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "SubscribeToUsersTyping{" +
                "mChatId='" + mChatId + '\'' +
                '}';
        }
    }
}

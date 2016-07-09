package com.badoo.chateau.core.repos.messages;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.badoo.barf.data.repo.annotations.Handles;
import com.badoo.chateau.core.model.Message;
import com.badoo.chateau.core.repos.messages.MessageQueries.GetUndeliveredQuery;
import com.badoo.chateau.core.repos.messages.MessageQueries.LoadQuery;
import com.badoo.chateau.core.repos.messages.MessageQueries.SendQuery;
import com.badoo.chateau.core.repos.messages.MessageQueries.SubscribeQuery;

import java.util.List;

import rx.Observable;


/**
 * Defines a data source providing messages for the {@link MessageRepository}
 */
public interface MessageDataSource<M extends Message> {

    class LoadResult<M extends Message> {

        private final List<M> mMessages;
        private final boolean mCanLoadOlder;
        private final boolean mCanLoadNewer;

        public LoadResult(@NonNull List<M> messages, boolean canLoadOlder, boolean canLoadNewer) {
            mMessages = messages;
            mCanLoadOlder = canLoadOlder;
            mCanLoadNewer = canLoadNewer;
        }

        @NonNull
        public List<M> getMessages() {
            return mMessages;
        }

        public boolean canLoadOlder() {
            return mCanLoadOlder;
        }

        public boolean canLoadNewer() {
            return mCanLoadNewer;
        }
    }

    class Update<M extends Message> {

        private final String mConversationId;
        private final Action mAction;
        private final M mNewMessage;
        private final M mOldMessage;

        public Update(@NonNull String conversationId, @NonNull Action action, @Nullable M oldMessage, @Nullable M newMessage) {
            mConversationId = conversationId;
            mAction = action;
            mNewMessage = newMessage;
            mOldMessage = oldMessage;
        }

        @NonNull
        public String getConversationId() {
            return mConversationId;
        }

        public Action getAction() {
            return mAction;
        }

        public M getNewMessage() {
            return mNewMessage;
        }

        public M getOldMessage() {
            return mOldMessage;
        }

        public enum Action {
            ADDED,
            UPDATED,
            READ,
            REMOVED,
            INVALIDATE_ALL
        }

        @Override
        public String toString() {
            return "Update{" +
                "mConversationId='" + mConversationId + '\'' +
                ", mAction=" + mAction +
                ", mNewMessage=" + mNewMessage +
                ", mOldMessage=" + mOldMessage +
                '}';
        }
    }

    /**
     * Instructs the data source to load more messages.
     */
    @NonNull
    @Handles(LoadQuery.class)
    Observable<LoadResult<M>> load(@NonNull LoadQuery<M> query);

    /**
     * Returns an Observable that will emit all messages that have failed to be delivered.
     */
    @NonNull
    @Handles(GetUndeliveredQuery.class)
    Observable<List<M>> getUndelivered(@NonNull GetUndeliveredQuery<M> query);

    /**
     * Sends a new message
     */
    @NonNull
    @Handles(SendQuery.class)
    Observable<Void> send(@NonNull SendQuery<M> query);

    /**
     * Returns an observable that will emit the messages in the data source as well as updates if the data changes.
     * These updates contain the entire data set.
     */
    @NonNull
    @Handles(SubscribeQuery.class)
    Observable<Update<M>> subscribe(@NonNull SubscribeQuery<M> query);

}

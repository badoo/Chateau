package com.badoo.chateau.core.repos.messages;

import android.support.annotation.NonNull;

import com.badoo.barf.data.repo.annotations.Handles;
import com.badoo.chateau.core.model.Message;
import com.badoo.chateau.core.repos.messages.MessageQueries.LoadMessagesQuery;
import com.badoo.chateau.core.repos.messages.MessageQueries.SendMessageQuery;
import com.badoo.chateau.core.repos.messages.MessageQueries.SubscribeToMessagesQuery;

import java.util.List;

import rx.Observable;


/**
 * Defines a data source providing messages for the {@link MessageRepository}
 */
public interface MessageDataSource<M extends Message> {

    /**
     * Instructs the data source to load more messages. The returned observable emits True if more data can (potentially be loaded)
     * Will emit False if you are loading older messages and have reached the end.
     */
    @NonNull
    @Handles(LoadMessagesQuery.class)
    Observable<Boolean> loadMessages(@NonNull LoadMessagesQuery<M> query);

    /**
     * Sends a new message
     */
    @Handles(SendMessageQuery.class)
    Observable<Void> sendMessage(@NonNull SendMessageQuery query);

    /**
     * Returns an observable that will emit the messages in the data source as well as updates if the data changes.
     * These updates contain the entire data set.
     */
    @NonNull
    @Handles(SubscribeToMessagesQuery.class)
    Observable<List<M>> subscribeToMessages(@NonNull SubscribeToMessagesQuery<M> query);

}

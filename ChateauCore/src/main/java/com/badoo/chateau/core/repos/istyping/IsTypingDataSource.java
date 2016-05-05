package com.badoo.chateau.core.repos.istyping;

import android.support.annotation.NonNull;

import com.badoo.barf.data.repo.annotations.Handles;
import com.badoo.chateau.core.model.User;

import rx.Observable;

/**
 * Defines a data source for providing information about whether a participant of a conversation is typing.
 */
public interface IsTypingDataSource<U extends User> {

    /**
     * Should be called when the user is typing a message.
     */
    @Handles(IsTypingQueries.SendIsTyping.class)
    Observable<Void> sendUserIsTyping(@NonNull IsTypingQueries.SendIsTyping query);

    /**
     * Returns an observable that will be updated whenever a user is typing.
     */
    @NonNull
    @Handles(IsTypingQueries.SubscribeToUsersTypingQuery.class)
    Observable<U> subscribeToUsersTyping(@NonNull IsTypingQueries.SubscribeToUsersTypingQuery query);

}

package com.badoo.chateau.core.repos.istyping;

import android.support.annotation.NonNull;

import com.badoo.barf.data.repo.annotations.Handles;
import com.badoo.chateau.core.model.User;

import rx.Observable;

public interface IsTypingDataSource {

    @Handles(IsTypingQuery.SendIsTyping.class)
    void sendUserIsTyping(@NonNull IsTypingQuery.SendIsTyping query);

    @NonNull
    @Handles(IsTypingQuery.SubscribeToUsersTyping.class)
    Observable<User> SubscribeToUsersTyping(@NonNull IsTypingQuery.SubscribeToUsersTyping query);

}

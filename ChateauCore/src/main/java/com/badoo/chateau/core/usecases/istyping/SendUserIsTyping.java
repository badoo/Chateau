package com.badoo.chateau.core.usecases.istyping;

import android.support.annotation.NonNull;

import com.badoo.barf.data.repo.Repository;
import com.badoo.barf.usecase.UseCase;
import com.badoo.chateau.core.model.User;
import com.badoo.chateau.core.repos.istyping.IsTypingQueries;

import rx.Observable;

/**
 * Use case to indicate that the user is typing a message in a chat.
 */
@UseCase
public class SendUserIsTyping {

    private final Repository<? extends User> mRepository;

    public SendUserIsTyping(@NonNull Repository<? extends User> repository) {
        mRepository = repository;
    }

    public Observable<Void> execute(@NonNull String conversationId) {
        return mRepository.query(new IsTypingQueries.SendIsTyping(conversationId)).map(user -> null);
    }
}

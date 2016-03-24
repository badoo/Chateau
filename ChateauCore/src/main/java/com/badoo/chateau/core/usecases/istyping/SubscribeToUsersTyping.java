package com.badoo.chateau.core.usecases.istyping;

import android.support.annotation.NonNull;

import com.badoo.barf.data.repo.Repositories;
import com.badoo.barf.usecase.UseCase;
import com.badoo.chateau.core.model.User;
import com.badoo.chateau.core.repos.istyping.IsTypingQuery;
import com.badoo.chateau.core.repos.istyping.IsTypingRepository;
import com.badoo.chateau.core.usecases.messages.ChatParams;

import rx.Observable;

/**
 * Use case to indicate that the user is typing a message in a chat.
 */
public class SubscribeToUsersTyping extends UseCase<ChatParams, User> {

    private final IsTypingRepository mRepo;

    public SubscribeToUsersTyping() {
        mRepo = Repositories.getRepo(IsTypingRepository.KEY);
    }

    SubscribeToUsersTyping(@NonNull IsTypingRepository repo) {
        mRepo = repo;
    }

    @Override
    protected Observable<User> createObservable(ChatParams params) {
        return mRepo.query(new IsTypingQuery.SubscribeToUsersTyping(params.mChatId));
    }
}

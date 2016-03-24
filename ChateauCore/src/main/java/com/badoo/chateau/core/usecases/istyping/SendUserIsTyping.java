package com.badoo.chateau.core.usecases.istyping;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.badoo.barf.data.repo.Repositories;
import com.badoo.barf.usecase.UseCase;
import com.badoo.chateau.core.repos.istyping.IsTypingQuery;
import com.badoo.chateau.core.repos.istyping.IsTypingRepository;
import com.badoo.chateau.core.usecases.messages.ChatParams;

import rx.Observable;

/**
 * Use case to indicate that the user is typing a message in a chat.
 */
public class SendUserIsTyping extends UseCase<ChatParams, Void> {

    private final IsTypingRepository mRepo;

    public SendUserIsTyping() {
       mRepo = Repositories.getRepo(IsTypingRepository.KEY);
    }

    @VisibleForTesting
    SendUserIsTyping(@NonNull IsTypingRepository repo) {
        mRepo = repo;
    }

    @Override
    protected Observable<Void> createObservable(ChatParams params) {
        return mRepo.query(new IsTypingQuery.SendIsTyping(params.mChatId)).map(user -> null);
    }
}

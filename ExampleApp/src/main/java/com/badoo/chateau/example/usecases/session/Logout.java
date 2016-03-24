package com.badoo.chateau.example.usecases.session;

import android.support.annotation.VisibleForTesting;

import com.badoo.barf.usecase.RepoUseCase;
import com.badoo.barf.usecase.UseCase;
import com.badoo.chateau.data.models.BaseUser;
import com.badoo.chateau.data.repos.session.SessionRepository;

import rx.Observable;

import static com.badoo.chateau.data.repos.session.SessionRepository.*;

public class Logout extends RepoUseCase<UseCase.NoParams, BaseUser, SessionRepository> {

    public Logout() {
        super(KEY);
    }

    @VisibleForTesting
    protected Logout(SessionRepository repository) {
        super(repository);
    }

    @Override
    protected Observable<BaseUser> createObservable(NoParams params) {
        return getRepo().query(SessionQuery.logout());
    }
}

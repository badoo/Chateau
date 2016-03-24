package com.badoo.chateau.example.usecases.session;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.badoo.barf.usecase.RepoUseCase;
import com.badoo.chateau.data.models.BaseUser;
import com.badoo.chateau.data.repos.session.SessionRepository;

import rx.Observable;

import static com.badoo.chateau.data.repos.session.SessionRepository.*;

public class Register extends RepoUseCase<Register.RegisterParams, BaseUser, SessionRepository> {

    public Register() {
        super(KEY);
    }

    @VisibleForTesting
    protected Register(SessionRepository repository) {
        super(repository);
    }

    @Override
    protected Observable<BaseUser> createObservable(RegisterParams params) {
        return getRepo().query(SessionQuery.register(params.mUserName, params.mDisplayName, params.mPassword));
    }

    public static final class RegisterParams {
        @NonNull
        final String mUserName;
        @NonNull
        final String mDisplayName;
        @NonNull
        final String mPassword;

        public RegisterParams(@NonNull String userName, @NonNull String displayName, @NonNull String password) {
            mUserName = userName;
            mDisplayName = displayName;
            mPassword = password;
        }

    }

}

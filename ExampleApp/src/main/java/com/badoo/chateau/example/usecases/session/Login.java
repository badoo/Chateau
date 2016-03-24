package com.badoo.chateau.example.usecases.session;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.badoo.barf.usecase.RepoUseCase;
import com.badoo.chateau.data.models.BaseUser;
import com.badoo.chateau.data.repos.session.SessionRepository;

import rx.Observable;

import static com.badoo.chateau.data.repos.session.SessionRepository.*;

public class Login extends RepoUseCase<Login.LoginParams, BaseUser, SessionRepository> {

    public Login() {
        super(SessionRepository.KEY);
    }

    @VisibleForTesting
    protected Login(SessionRepository repository) {
        super(repository);
    }

    @Override
    protected Observable<BaseUser> createObservable(LoginParams params) {
        return getRepo().query(SessionQuery.login(params.mUserName, params.mPassword));
    }

    public static final class LoginParams {
        @NonNull
        final String mUserName;
        @NonNull
        final String mPassword;

        public LoginParams(@NonNull String userName, @NonNull String password) {
            mUserName = userName;
            mPassword = password;
        }

    }
}

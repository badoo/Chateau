package com.badoo.chateau.example.usecases.session;

import android.support.annotation.NonNull;

import com.badoo.barf.data.repo.Repository;
import com.badoo.barf.usecase.UseCase;
import com.badoo.chateau.core.model.User;
import com.badoo.chateau.example.data.repos.session.SessionQuery;

import rx.Observable;

@UseCase
public class Register<U extends User> {

    private final Repository<U> mRepository;

    public Register(@NonNull Repository<U> repository) {
        mRepository = repository;
    }

    public Observable<U> execute(@NonNull String userName, @NonNull String displayName, @NonNull String password) {
        return mRepository.query(new SessionQuery.Register<>(userName, displayName, password));
    }
}

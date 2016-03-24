package com.badoo.chateau.core.usecases.users;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.badoo.barf.usecase.RepoUseCase;
import com.badoo.barf.usecase.UseCase;
import com.badoo.chateau.core.model.User;
import com.badoo.chateau.core.repos.users.UserQuery;
import com.badoo.chateau.core.repos.users.UserRepository;

import java.util.List;

import rx.Observable;

public class GetUsers extends RepoUseCase<UseCase.NoParams, List<User>, UserRepository> {

    public GetUsers() {
        super(UserRepository.KEY);
    }

    @VisibleForTesting
    protected GetUsers(@NonNull UserRepository repository) {
        super(repository);
    }

    @Override
    protected Observable<List<User>> createObservable(NoParams params) {
        return getRepo().query(new UserQuery.GetAllUsersQuery()).toList();
    }
}

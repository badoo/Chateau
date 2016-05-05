package com.badoo.chateau.core.usecases.users;

import android.support.annotation.NonNull;

import com.badoo.barf.data.repo.Repository;
import com.badoo.barf.usecase.UseCase;
import com.badoo.chateau.core.model.User;
import com.badoo.chateau.core.repos.users.UserQueries;

import java.util.List;

import rx.Observable;

/**
 * Use case for retrieving all users
 */
@UseCase
public class GetUsers<U extends User> {

    private final Repository<U> mRepository;

    public GetUsers(@NonNull Repository<U> repository) {
        mRepository = repository;
    }

    public Observable<List<U>> execute() {
        return mRepository.query(new UserQueries.GetAllUsersQuery<>());
    }
}

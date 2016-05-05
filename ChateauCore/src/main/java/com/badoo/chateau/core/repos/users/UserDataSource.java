package com.badoo.chateau.core.repos.users;

import android.support.annotation.NonNull;

import com.badoo.barf.data.repo.annotations.Handles;
import com.badoo.chateau.core.model.User;
import com.badoo.chateau.core.repos.users.UserQueries.GetAllUsersQuery;
import com.badoo.chateau.core.repos.users.UserQueries.GetUserQuery;

import java.util.List;

import rx.Observable;

/**
 * Defines a data source providing user data for the {@link UserRepository}
 */
public interface UserDataSource<U extends User> {

    /**
     * Returns an {@link Observable} which emits all the users
     */
    @NonNull
    @Handles(GetAllUsersQuery.class)
    Observable<List<U>> getAllUsers(GetAllUsersQuery query);

    /**
     * Returns an {@link Observable} which emits a single, specific user
     */
    @NonNull
    @Handles(GetUserQuery.class)
    Observable<U> getSingleUser(GetUserQuery query);

}

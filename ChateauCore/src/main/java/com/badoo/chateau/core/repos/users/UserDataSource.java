package com.badoo.chateau.core.repos.users;

import android.support.annotation.NonNull;

import com.badoo.barf.data.repo.annotations.Handles;
import com.badoo.chateau.core.model.User;
import com.badoo.chateau.core.repos.users.UserQuery.GetAllUsersQuery;
import com.badoo.chateau.core.repos.users.UserQuery.GetUserQuery;

import rx.Observable;


public interface UserDataSource {

    /**
     * Returns an {@link Observable} which emits all the users
     */
    @NonNull
    @Handles(GetAllUsersQuery.class)
    Observable<User> getAllUsers(GetAllUsersQuery query);

    /**
     * Returns an {@link Observable} which emits a single, specific user
     */
    @NonNull
    @Handles(GetUserQuery.class)
    Observable<User> getSingleUser(GetUserQuery query);

}

package com.badoo.chateau.example.data.repos.session;

import android.support.annotation.NonNull;

import com.badoo.barf.data.repo.annotations.Handles;
import com.badoo.chateau.data.models.BaseUser;

import rx.Observable;

/**
 * Defines a data source for providing session data for the {@link SessionRepository}
 */
public interface SessionDataSource {

    /**
     * Attempt to sign in as a user with a given name and password
     *
     * @return an {@link Observable} which emits the signed in in user.
     */
    @NonNull
    @Handles(SessionQuery.SignIn.class)
    Observable<BaseUser> signIn(@NonNull SessionQuery.SignIn query);

    /**
     * Signs out the currently signed in user.
     */
    @Handles(SessionQuery.SignOut.class)
    Observable<Void> signOut();

    /**
     * Attempt to register a user.
     * <p>
     * @return an {@link Observable} which emits the registered user.
     */
    @NonNull
    @Handles(SessionQuery.Register.class)
    Observable<BaseUser> register(@NonNull SessionQuery.Register query);
}

package com.badoo.chateau.data.repos.session;

import android.support.annotation.NonNull;

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
    Observable<BaseUser> signIn(@NonNull String username, @NonNull String password);

    /**
     * Signs out the currently signed in user.
     */
    Observable<Void> signOut();

    /**
     * Attempt to register a user.
     * <p>
     * @return an {@link Observable} which emits the registered user.
     */
    @NonNull
    Observable<BaseUser> register(@NonNull String userName, @NonNull String displayName, @NonNull String password);
}

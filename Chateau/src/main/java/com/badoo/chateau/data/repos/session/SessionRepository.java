package com.badoo.chateau.data.repos.session;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.badoo.barf.data.repo.Query;
import com.badoo.barf.data.repo.Repositories;
import com.badoo.barf.data.repo.Repository;
import com.badoo.chateau.data.models.BaseUser;

import rx.Observable;

public class SessionRepository implements Repository<SessionRepository.SessionQuery, BaseUser> {

    public final static Repositories.Key<SessionRepository> KEY = new Repositories.Key<>("SessionRepository");

    private SessionDataSource mDataSource;

    public SessionRepository(@NonNull SessionDataSource dataSource) {
        mDataSource = dataSource;
    }

    @NonNull
    @Override
    public Observable<BaseUser> query(@NonNull SessionQuery query) {
        switch (query.mType) {
            case LOGIN:
                return mDataSource.signIn(query.mUserName, query.mPassword);
            case LOGOUT:
                return mDataSource.signOut().cast(BaseUser.class);
            case REGISTER:
                return mDataSource.register(query.mUserName, query.mDisplayName, query.mPassword);
            default:
                throw new RuntimeException("Unknown query type " + query);
        }
    }

    public static final class SessionQuery implements Query {

        public static SessionQuery login(@NonNull String userName, @NonNull String password) {
            return new SessionQuery(Type.LOGIN, userName, password, null);
        }

        public static SessionQuery logout() {
            return new SessionQuery(Type.LOGIN, null, null, null);
        }

        public static SessionQuery register(@NonNull String userName, @Nullable String displayName, @NonNull String password) {
            return new SessionQuery(Type.REGISTER, userName, password, displayName);
        }

        private enum Type {
            LOGIN, LOGOUT, REGISTER
        }

        @NonNull
        final Type mType;
        @Nullable
        final String mUserName;
        @Nullable
        final String mPassword;
        @Nullable
        final String mDisplayName;

        private SessionQuery(@NonNull Type type, @Nullable String userName, @Nullable String password, @Nullable String displayName) {
            mType = type;
            mUserName = userName;
            mPassword = password;
            mDisplayName = displayName;
        }
    }
}

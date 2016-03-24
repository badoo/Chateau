package com.badoo.chateau.core.repos.users;

import android.support.annotation.NonNull;

import com.badoo.barf.data.repo.Query;

public abstract class UserQuery implements Query {

    public static class GetAllUsersQuery extends UserQuery {

        public GetAllUsersQuery() {
        }

        @Override
        public boolean equals(Object o) {
            return o != null && o instanceof GetAllUsersQuery;
        }

        @Override
        public int hashCode() {
            return 31;
        }

        @Override
        public String toString() {
            return "GetAllUsersQuery{}";
        }
    }

    public static class GetUserQuery extends UserQuery {

        @NonNull
        final String mUserId;

        public GetUserQuery(@NonNull String userId) {
            mUserId = userId;
        }

        @NonNull
        public String getUserId() {
            return mUserId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            GetUserQuery that = (GetUserQuery) o;

            return mUserId.equals(that.mUserId);

        }

        @Override
        public int hashCode() {
            return mUserId.hashCode();
        }

        @Override
        public String toString() {
            return "GetUserQuery{" +
                "mUserId='" + mUserId + '\'' +
                '}';
        }
    }

}

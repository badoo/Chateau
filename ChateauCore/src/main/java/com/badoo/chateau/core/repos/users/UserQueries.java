package com.badoo.chateau.core.repos.users;

import android.support.annotation.NonNull;

import com.badoo.barf.data.repo.Query;
import com.badoo.chateau.core.model.User;

import java.util.List;


/**
 * Query class for performing operations on the UserRepository
 */
public abstract class UserQueries  {

    /**
     * Query for requesting all users
     */
    public static class GetAllUsersQuery<U extends User> implements Query<List<U>> {

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

    /**
     * Query for requesting a single
     */
    public static class GetUserQuery<U extends User> implements Query<U> {

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

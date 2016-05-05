package com.badoo.chateau.example.data.repos.session;

import android.support.annotation.NonNull;

import com.badoo.barf.data.repo.Query;
import com.badoo.chateau.core.model.User;

/**
 * Query class for performing operations on the SessionRepository
 */
public abstract class SessionQuery {

    /**
     * Query for signing in to the app
     */
    public static class SignIn<U extends User> implements Query<U>  {

        @NonNull
        final String mUserName;
        @NonNull
        final String mPassword;

        public SignIn(@NonNull String userName, @NonNull String password) {
            mUserName = userName;
            mPassword = password;
        }

        @NonNull
        public String getUserName() {
            return mUserName;
        }

        @NonNull
        public String getPassword() {
            return mPassword;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SignIn signIn = (SignIn) o;

            if (!mUserName.equals(signIn.mUserName)) return false;
            return mPassword.equals(signIn.mPassword);

        }

        @Override
        public int hashCode() {
            int result = mUserName.hashCode();
            result = 31 * result + mPassword.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "SignIn{" +
                "mUserName='" + mUserName + '\'' +
                ", mPassword='" + mPassword + '\'' +
                '}';
        }
    }

    /**
     * Query for registering a new account
     */
    public static class Register<U extends User> implements Query<U> {

        @NonNull
        final String mUserName;
        @NonNull
        final String mPassword;
        @NonNull
        final String mDisplayName;

        public Register(@NonNull String userName, @NonNull String displayName, @NonNull String password) {
            mUserName = userName;
            mPassword = password;
            mDisplayName = displayName;
        }

        @NonNull
        public String getUserName() {
            return mUserName;
        }

        @NonNull
        public String getPassword() {
            return mPassword;
        }

        @NonNull
        public String getDisplayName() {
            return mDisplayName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Register register = (Register) o;

            if (!mUserName.equals(register.mUserName)) return false;
            if (!mPassword.equals(register.mPassword)) return false;
            return mDisplayName.equals(register.mDisplayName);

        }

        @Override
        public int hashCode() {
            int result = mUserName.hashCode();
            result = 31 * result + mPassword.hashCode();
            result = 31 * result + mDisplayName.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "Register{" +
                "mUserName='" + mUserName + '\'' +
                ", mPassword='" + mPassword + '\'' +
                ", mDisplayName='" + mDisplayName + '\'' +
                '}';
        }
    }

    /**
     * Query for signing out of the app
     */
    public static class SignOut implements Query<Void> {

        @Override
        public boolean equals(Object o) {
            return o instanceof SignOut;
        }

        @Override
        public int hashCode() {
            return 1;
        }
    }

}

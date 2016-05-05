package com.badoo.chateau.example.ui.session.login;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.badoo.barf.mvp.FlowListener;
import com.badoo.barf.mvp.MvpPresenter;
import com.badoo.barf.mvp.MvpView;

public interface LoginPresenter extends MvpPresenter {

    void onSignIn(@NonNull String userName, @NonNull String password);

    void onNotRegistered();

    interface LoginFlowListener extends FlowListener {

        void userLoggedIn();

        void userNotRegistered();
    }

    interface LoginView extends MvpView {

        void showUserNameEmptyError();

        void showPasswordEmptyError();

        /**
         * Show an error message to the user (if the error warrants it)
         *
         * @param fatal true if the error was fatal, false if it can be ignored while still maintaining some functionality.
         */
        void showError(boolean fatal, @Nullable Throwable throwable);

        void clearAllErrors();

        void displayProgress();

        void hideProgress();

    }
}

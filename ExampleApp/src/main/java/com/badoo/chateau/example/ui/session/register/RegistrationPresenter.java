package com.badoo.chateau.example.ui.session.register;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.badoo.barf.mvp.FlowListener;
import com.badoo.barf.mvp.MvpPresenter;
import com.badoo.barf.mvp.MvpView;

public interface RegistrationPresenter extends MvpPresenter {

    void onRegister(@NonNull String userName, @NonNull String displayName, @NonNull String password);

    void onAlreadyRegistered();

    interface RegistrationFlowListener extends FlowListener {

        void userRegistered();

        void userAlreadyRegistered();
    }

    interface RegistrationView extends MvpView {

        void showUserNameEmptyError();

        void showDisplayNameEmptyError();

        void showPasswordEmptyError();

        /**
         * Show an error message to the user (if the error warrants it)
         *
         * @param fatal true if the error was fatal, false if it can be ignored while still maintaining some functionality.
         */
        void showError(boolean fatal, @Nullable Throwable throwable);

        void clearAllErrors();

        void showProgress();

        void hideProgress();
    }
}

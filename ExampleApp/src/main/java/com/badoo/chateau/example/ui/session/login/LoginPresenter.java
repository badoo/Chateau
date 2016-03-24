package com.badoo.chateau.example.ui.session.login;

import android.support.annotation.NonNull;

import com.badoo.barf.mvp.Presenter;

public interface LoginPresenter extends Presenter<LoginView, LoginPresenter.LoginFlowListener> {

    void onSignIn(@NonNull String userName, @NonNull String password);

    void onNotRegistered();

    interface LoginFlowListener extends Presenter.FlowListener {

        void userLoggedIn();

        void userNotRegistered();
    }
}

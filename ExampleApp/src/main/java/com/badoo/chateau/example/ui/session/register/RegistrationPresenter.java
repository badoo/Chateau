package com.badoo.chateau.example.ui.session.register;

import android.support.annotation.NonNull;

import com.badoo.barf.mvp.Presenter;

public interface RegistrationPresenter extends Presenter<RegistrationView, RegistrationPresenter.RegistrationFlowListener> {

    void onRegister(@NonNull String userName, @NonNull String displayName, @NonNull String password);

    void onAlreadyRegistered();

    interface RegistrationFlowListener extends Presenter.FlowListener {

        void userRegistered();

        void userAlreadyRegistered();
    }
}

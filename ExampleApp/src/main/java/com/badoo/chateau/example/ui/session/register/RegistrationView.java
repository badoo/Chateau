package com.badoo.chateau.example.ui.session.register;

import android.support.annotation.StringRes;

import com.badoo.barf.mvp.View;

public interface RegistrationView extends View<RegistrationPresenter> {

    void showUserNameEmptyError();

    void showDisplayNameEmptyError();

    void showPasswordEmptyError();

    void showGenericError(@StringRes int errorMessage);

    void clearAllErrors();

    void showProgress();

    void hideProgress();
}

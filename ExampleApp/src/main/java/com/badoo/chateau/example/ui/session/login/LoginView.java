package com.badoo.chateau.example.ui.session.login;

import android.support.annotation.StringRes;

import com.badoo.barf.mvp.View;

public interface LoginView extends View<LoginPresenter> {

    void showUserNameEmptyError();

    void showPasswordEmptyError();

    void showGenericError(@StringRes int errorMessage);

    void clearAllErrors();

    void displayProgress();

    void hideProgress();

}

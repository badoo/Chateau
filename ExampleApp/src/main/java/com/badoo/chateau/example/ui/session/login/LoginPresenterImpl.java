package com.badoo.chateau.example.ui.session.login;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;

import com.badoo.barf.mvp.BasePresenter;
import com.badoo.chateau.example.R;
import com.badoo.chateau.example.usecases.session.Login;

import rx.Subscription;

import static com.badoo.chateau.example.usecases.session.Login.LoginParams;

public class LoginPresenterImpl extends BasePresenter<LoginView, LoginPresenter.LoginFlowListener> implements LoginPresenter {

    @NonNull
    private final Login mLogin;

    public LoginPresenterImpl() {
        this(new Login());
    }

    @VisibleForTesting
    LoginPresenterImpl(@NonNull Login login) {
        mLogin = login;
    }

    @Override
    public void onSignIn(@NonNull String userName, @NonNull String password) {
        LoginView view = getView();
        view.clearAllErrors();
        boolean error = false;
        if (TextUtils.isEmpty(userName)) {
            view.showUserNameEmptyError();
            error = true;
        }
        if (TextUtils.isEmpty(password)) {
            view.showPasswordEmptyError();
            error = true;
        }

        if (!error) {
            view.displayProgress();

            final LoginParams params = new LoginParams(userName, password);
            final Subscription loginSub = mLogin.execute(params).subscribe(
                user -> {
                    getFlowListener().userLoggedIn();
                },
                throwable -> {
                    view.hideProgress();
                    view.showGenericError(R.string.error_login);
                });
            trackSubscription(loginSub);
        }
    }

    @Override
    public void onNotRegistered() {
        getFlowListener().userNotRegistered();
    }

}

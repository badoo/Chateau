package com.badoo.chateau.example.ui.session.login;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;

import com.badoo.barf.mvp.BaseRxPresenter;
import com.badoo.barf.rx.ScheduleOn;
import com.badoo.chateau.core.model.User;
import com.badoo.chateau.example.usecases.session.SignIn;

import rx.Subscription;

public class LoginPresenterImpl<U extends User> extends BaseRxPresenter implements LoginPresenter {

    private static final String TAG = LoginPresenterImpl.class.getSimpleName();

    @NonNull
    private final LoginView mView;
    @NonNull
    private final LoginFlowListener mFlowListener;
    @NonNull
    private final SignIn<U> mSignIn;

    @VisibleForTesting
    LoginPresenterImpl(@NonNull LoginView view, @NonNull LoginFlowListener flowListener, @NonNull SignIn<U> signIn) {
        mView = view;
        mFlowListener = flowListener;
        mSignIn = signIn;
    }

    @Override
    public void onSignIn(@NonNull String userName, @NonNull String password) {
        mView.clearAllErrors();
        boolean error = false;
        if (TextUtils.isEmpty(userName)) {
            mView.showUserNameEmptyError();
            error = true;
        }
        if (TextUtils.isEmpty(password)) {
            mView.showPasswordEmptyError();
            error = true;
        }

        if (!error) {
            mView.displayProgress();

            final Subscription loginSub = mSignIn.execute(userName, password)
                .compose(ScheduleOn.io()).subscribe(
                user -> {
                    mFlowListener.userLoggedIn();
                },
                throwable -> {
                    mView.hideProgress();
                    onFatalError(throwable);
                });
            manage(loginSub);
        }
    }

    @Override
    public void onNotRegistered() {
        mFlowListener.userNotRegistered();
    }

    private void onFatalError(Throwable throwable) {
        Log.e(TAG, "Fatal error", throwable);
        mView.showError(true, throwable);
    }

}

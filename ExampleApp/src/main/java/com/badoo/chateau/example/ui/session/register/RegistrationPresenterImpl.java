package com.badoo.chateau.example.ui.session.register;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;

import com.badoo.barf.mvp.BasePresenter;
import com.badoo.chateau.example.R;
import com.badoo.chateau.example.usecases.session.Register;

import rx.Subscription;


public class RegistrationPresenterImpl extends BasePresenter<RegistrationView, RegistrationPresenter.RegistrationFlowListener> implements RegistrationPresenter {

    @NonNull
    private final Register mRegister;

    public RegistrationPresenterImpl() {
        this(new Register());
    }

    @VisibleForTesting
    RegistrationPresenterImpl(@NonNull Register register) {
        mRegister = register;
    }

    @Override
    public void onRegister(@NonNull String userName, @NonNull String displayName, @NonNull String password) {
        RegistrationView view = getView();
        view.clearAllErrors();
        boolean error = false;
        if (TextUtils.isEmpty(userName)) {
            view.showUserNameEmptyError();
            error = true;
        }
        if (TextUtils.isEmpty(displayName)) {
            view.showDisplayNameEmptyError();
            error = true;
        }
        if (TextUtils.isEmpty(password)) {
            view.showPasswordEmptyError();
            error = true;
        }

        if (!error) {
            view.showProgress();

            final Register.RegisterParams params = new Register.RegisterParams(userName, displayName, password);
            final Subscription registerSub = mRegister.execute(params).subscribe(user -> {
                getFlowListener().userRegistered();
            }, throwable -> {
                view.hideProgress();
                view.showGenericError(R.string.error_registration);
            });
            trackSubscription(registerSub);
        }
    }

    @Override
    public void onAlreadyRegistered() {
        getFlowListener().userAlreadyRegistered();
    }

}

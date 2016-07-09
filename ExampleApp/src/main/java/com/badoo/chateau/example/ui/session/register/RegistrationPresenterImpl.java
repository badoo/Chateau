package com.badoo.chateau.example.ui.session.register;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.badoo.barf.mvp.BaseRxPresenter;
import com.badoo.barf.rx.ScheduleOn;
import com.badoo.chateau.core.model.User;
import com.badoo.chateau.example.usecases.session.Register;

import rx.Subscription;


public class RegistrationPresenterImpl<U extends User> extends BaseRxPresenter implements RegistrationPresenter {

    private static final String TAG = RegistrationPresenterImpl.class.getSimpleName();

    @NonNull
    private final RegistrationView mView;
    @NonNull
    private final RegistrationFlowListener mFlowListener;
    @NonNull
    private final Register<U> mRegister;


    public RegistrationPresenterImpl(@NonNull RegistrationView view, @NonNull RegistrationFlowListener flowListener, @NonNull Register<U> register) {
        mView = view;
        mFlowListener = flowListener;
        mRegister = register;
    }

    @Override
    public void onRegister(@NonNull String userName, @NonNull String displayName, @NonNull String password) {
        mView.clearAllErrors();
        boolean error = false;
        if (TextUtils.isEmpty(userName)) {
            mView.showUserNameEmptyError();
            error = true;
        }
        if (TextUtils.isEmpty(displayName)) {
            mView.showDisplayNameEmptyError();
            error = true;
        }
        if (TextUtils.isEmpty(password)) {
            mView.showPasswordEmptyError();
            error = true;
        }

        if (!error) {
            mView.showProgress();

            final Subscription registerSub = mRegister.execute(userName, displayName, password)
                .compose(ScheduleOn.io()).subscribe(user -> {
                mFlowListener.userRegistered();
            }, throwable -> {
                mView.hideProgress();
                onFatalError(throwable);
            });
            manage(registerSub);
        }
    }

    @Override
    public void onAlreadyRegistered() {
        mFlowListener.userAlreadyRegistered();
    }

    private void onFatalError(Throwable throwable) {
        Log.e(TAG, "Fatal error", throwable);
        mView.showError(true, throwable);
    }

}

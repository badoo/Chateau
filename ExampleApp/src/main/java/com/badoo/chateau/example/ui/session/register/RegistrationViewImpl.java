package com.badoo.chateau.example.ui.session.register;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.badoo.barf.mvp.MvpView;
import com.badoo.barf.mvp.PresenterFactory;
import com.badoo.chateau.example.R;
import com.badoo.chateau.example.ui.session.register.RegistrationPresenter.RegistrationView;
import com.badoo.chateau.extras.ViewFinder;

class RegistrationViewImpl implements RegistrationView, View.OnClickListener, MvpView {

    private final TextInputLayout mUserName;
    private final TextInputLayout mDisplayName;
    private final TextInputLayout mPassword;

    private final View mParent;
    private final View mRegisterFormView;
    private final ContentLoadingProgressBar mProgressView;
    @NonNull
    private final RegistrationPresenter mPresenter;

    private int mShortAnimTime;

    public RegistrationViewImpl(@NonNull ViewFinder viewFinder,
                                @NonNull PresenterFactory<RegistrationView, RegistrationPresenter> presenterFactory) {
        mPresenter = presenterFactory.init(this);
        mUserName = viewFinder.findViewById(R.id.register_username);
        mDisplayName = viewFinder.findViewById(R.id.register_displayName);
        mPassword = viewFinder.findViewById(R.id.register_password);

        final Button register = viewFinder.findViewById(R.id.register_register_button);
        register.setOnClickListener(this);

        final View alreadyRegistered = viewFinder.findViewById(R.id.register_already_registered_button);
        alreadyRegistered.setOnClickListener(this);

        mParent = viewFinder.findViewById(R.id.register_parent);
        mRegisterFormView = viewFinder.findViewById(R.id.register_form);
        mProgressView = viewFinder.findViewById(R.id.register_progress);

        mShortAnimTime = register.getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    @Override
    public void showUserNameEmptyError() {
        mUserName.setError(mUserName.getResources().getString(R.string.error_field_required));
    }

    @Override
    public void showDisplayNameEmptyError() {
        mDisplayName.setError(mUserName.getResources().getString(R.string.error_field_required));
    }

    @Override
    public void showPasswordEmptyError() {
        mPassword.setError(mUserName.getResources().getString(R.string.error_field_required));
    }

    @Override
    public void showError(boolean fatal, @Nullable Throwable throwable) {
        if (fatal) {
            Snackbar.make(mParent, mParent.getResources().getString(R.string.error_registration), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void clearAllErrors() {
        mUserName.setErrorEnabled(false);
        mDisplayName.setErrorEnabled(false);
        mPassword.setErrorEnabled(false);
    }

    @Override
    public void showProgress() {
        showProgress(true);
    }

    @Override
    public void hideProgress() {
        showProgress(false);
    }

    /**
     * Shows the progress UI and hides the registration form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(mShortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
            if (show) {
                mProgressView.show();
            }
            else {
                mProgressView.hide();
            }
        }
        else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            if (show) {
                mProgressView.show();
            }
            else {
                mProgressView.hide();
            }
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.register_register_button) {
            mPresenter.onRegister(
                mUserName.getEditText().getText().toString().trim(),
                mDisplayName.getEditText().getText().toString(),
                mPassword.getEditText().getText().toString());
            Context context = v.getContext();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        else if (v.getId() == R.id.register_already_registered_button) {
            mPresenter.onAlreadyRegistered();
        }
    }
}

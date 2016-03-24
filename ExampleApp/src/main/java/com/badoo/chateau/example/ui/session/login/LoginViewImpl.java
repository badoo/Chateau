package com.badoo.chateau.example.ui.session.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.badoo.barf.mvp.BaseView;
import com.badoo.chateau.example.R;
import com.badoo.chateau.example.ui.util.ViewFinder;

class LoginViewImpl extends BaseView<LoginPresenter> implements LoginView, View.OnClickListener {

    private final TextInputLayout mUserName;
    private final TextInputLayout mPassword;

    private final View mParent;
    private final View mRegisterFormView;
    private final ContentLoadingProgressBar mProgressView;

    private int mShortAnimTime;

    public LoginViewImpl(@NonNull ViewFinder viewFinder) {
        mUserName = viewFinder.findViewById(R.id.login_username);
        mPassword = viewFinder.findViewById(R.id.login_password);

        final Button signIn = viewFinder.findViewById(R.id.login_sign_in_button);
        signIn.setOnClickListener(this);

        final View alreadyRegistered = viewFinder.findViewById(R.id.login_not_registered_button);
        alreadyRegistered.setOnClickListener(this);

        mParent = viewFinder.findViewById(R.id.login_parent);
        mRegisterFormView = viewFinder.findViewById(R.id.login_form);
        mProgressView = viewFinder.findViewById(R.id.login_progress);

        mShortAnimTime = signIn.getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    @Override
    public void showUserNameEmptyError() {
        mUserName.setError(mUserName.getResources().getString(R.string.error_field_required));
    }

    @Override
    public void showPasswordEmptyError() {
        mPassword.setError(mUserName.getResources().getString(R.string.error_field_required));
    }

    @Override
    public void showGenericError(@StringRes int errorMessage) {
        Snackbar.make(mParent, errorMessage, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void clearAllErrors() {
        mUserName.setErrorEnabled(false);
        mPassword.setErrorEnabled(false);
    }

    @Override
    public void displayProgress() {
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
        if (v.getId() == R.id.login_sign_in_button) {
            getPresenter().onSignIn(
                mUserName.getEditText().getText().toString(),
                mPassword.getEditText().getText().toString());
            Context context = v.getContext();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        else if (v.getId() == R.id.login_not_registered_button) {
            getPresenter().onNotRegistered();
        }
    }
}

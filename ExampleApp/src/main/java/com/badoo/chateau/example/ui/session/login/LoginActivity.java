package com.badoo.chateau.example.ui.session.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.badoo.chateau.example.R;
import com.badoo.chateau.example.ui.BaseActivity;
import com.badoo.chateau.example.ui.Injector;
import com.badoo.chateau.example.ui.conversations.list.ConversationListActivity;
import com.badoo.chateau.example.ui.session.register.RegisterActivity;
import com.badoo.chateau.extras.ViewFinder;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity implements LoginPresenter.LoginFlowListener {

    public static class DefaultConfiguration extends Injector.BaseConfiguration<LoginActivity> {

        @Override
        public void inject(LoginActivity target) {
            final LoginView view = new LoginViewImpl(ViewFinder.from(target));
            final LoginPresenter presenter = createPresenter();
            bind(view, presenter, target);
            target.setLoginPresenter(presenter);
        }

        protected LoginPresenter createPresenter() {
            return new LoginPresenterImpl();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Injector.inject(this);
    }

    void setLoginPresenter(@NonNull LoginPresenter presenter) {
        registerPresenter(presenter);
    }

    @Override
    public void userLoggedIn() {
        openConversationList();
    }

    @Override
    public void userNotRegistered() {
        openRegistration();
    }

    private void openConversationList() {
        final Intent intent = new Intent(this, ConversationListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void openRegistration() {
        final Intent intent = new Intent(this, RegisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}


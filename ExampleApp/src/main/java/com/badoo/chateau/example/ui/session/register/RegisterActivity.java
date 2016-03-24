package com.badoo.chateau.example.ui.session.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.badoo.chateau.example.R;
import com.badoo.chateau.example.ui.BaseActivity;
import com.badoo.chateau.example.ui.Injector;
import com.badoo.chateau.example.ui.conversations.list.ConversationListActivity;
import com.badoo.chateau.example.ui.session.login.LoginActivity;
import com.badoo.chateau.example.ui.util.ViewFinder;

public class RegisterActivity extends BaseActivity implements RegistrationPresenter.RegistrationFlowListener {

    public static class DefaultConfiguration extends Injector.BaseConfiguration<RegisterActivity> {

        @Override
        public void inject(RegisterActivity target) {
            RegistrationView view = new RegistrationViewImpl(ViewFinder.from(target));
            final RegistrationPresenter presenter = createPresenter();
            bind(view, presenter, target);
            target.setRegistrationPresenter(presenter);
        }

        protected RegistrationPresenter createPresenter() {
            return new RegistrationPresenterImpl();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Injector.inject(this);
    }

    void setRegistrationPresenter(@NonNull RegistrationPresenter presenter) {
        registerPresenter(presenter);
    }

    @Override
    public void userRegistered() {
        openConversationsList();
    }

    @Override
    public void userAlreadyRegistered() {
        openLogin();
    }

    private void openConversationsList() {
        final Intent intent = new Intent(this, ConversationListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void openLogin() {
        final Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}

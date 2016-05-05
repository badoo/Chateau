package com.badoo.chateau.example.ui.session.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.badoo.barf.mvp.PresenterFactory;
import com.badoo.chateau.example.R;
import com.badoo.chateau.example.data.model.ExampleUser;
import com.badoo.chateau.example.ui.BaseActivity;
import com.badoo.chateau.example.ui.ExampleConfiguration;
import com.badoo.chateau.example.ui.Injector;
import com.badoo.chateau.example.ui.conversations.list.ConversationListActivity;
import com.badoo.chateau.example.ui.session.login.LoginActivity;
import com.badoo.chateau.example.ui.session.register.RegistrationPresenter.RegistrationView;
import com.badoo.chateau.example.usecases.session.Register;
import com.badoo.chateau.extras.ViewFinder;

import static com.badoo.chateau.example.ui.session.register.RegistrationPresenter.*;

public class RegisterActivity extends BaseActivity implements RegistrationFlowListener {

    public static class DefaultConfiguration extends ExampleConfiguration<RegisterActivity> {

        @Override
        public void inject(RegisterActivity target) {
            final PresenterFactory<RegistrationView, RegistrationPresenter> presenterFactory = new PresenterFactory<>(v -> createRegistrationPresenter(v, target));
            new RegistrationViewImpl(ViewFinder.from(target), presenterFactory);
            target.registerPresenter(presenterFactory.get());
        }

        protected RegistrationPresenterImpl<ExampleUser> createRegistrationPresenter(@NonNull RegistrationView view, @NonNull RegistrationFlowListener flowListener) {
            return new RegistrationPresenterImpl<>(view, flowListener, new Register<>(getSessionRepo()));
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
        finish();
        startActivity(intent);
    }

    private void openLogin() {
        final Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}

package com.badoo.chateau.example.ui.session.login;

import android.support.test.espresso.action.ViewActions;
import android.support.test.runner.AndroidJUnit4;

import com.badoo.chateau.example.BaseTestCase;
import com.badoo.chateau.example.R;
import com.badoo.chateau.example.ui.Injector;

import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest extends BaseTestCase<LoginActivity> {

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    private LoginPresenter mPresenter;

    @Override
    protected void beforeActivityLaunched() {
        mPresenter = mock(LoginPresenter.class);
        Injector.register(LoginActivity.class, new LoginActivity.DefaultConfiguration() {

            @Override
            protected LoginPresenter createPresenter() {
                return mPresenter;
            }
        });
    }

    @Override
    protected Class<LoginActivity> getActivityClass() {
        return LoginActivity.class;
    }

    @Test
    public void signinWithEmptyData() {
        // When
        onView(withId(R.id.login_sign_in_button)).perform(click());

        // Then
        verify(mPresenter).onSignIn("", "");
    }

    @Test
    public void gotoRegisterScreen() {
        // When
        onView(withId(R.id.login_not_registered_button)).perform(click());

        // Then
        verify(mPresenter).onNotRegistered();
    }

    @Test
    public void signinWithAllDetails() {
        // Given
        onView(withId(R.id.login_username_inner)).perform(ViewActions.typeText(USERNAME));
        onView(withId(R.id.login_password_inner)).perform(ViewActions.typeText(PASSWORD));

        // When
        closeSoftKeyboard();
        onView(withId(R.id.login_sign_in_button)).perform(click());
        verify(mPresenter).onSignIn(USERNAME, PASSWORD);
    }

}
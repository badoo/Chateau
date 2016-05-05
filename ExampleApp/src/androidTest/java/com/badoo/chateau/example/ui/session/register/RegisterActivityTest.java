package com.badoo.chateau.example.ui.session.register;

import android.support.annotation.NonNull;
import android.support.test.espresso.action.ViewActions;
import android.support.test.runner.AndroidJUnit4;

import com.badoo.chateau.example.BaseTestCase;
import com.badoo.chateau.example.R;
import com.badoo.chateau.example.data.model.ExampleUser;
import com.badoo.chateau.example.ui.Injector;
import com.badoo.chateau.example.ui.session.register.RegistrationPresenter.RegistrationFlowListener;
import com.badoo.chateau.example.ui.session.register.RegistrationPresenter.RegistrationView;

import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class RegisterActivityTest extends BaseTestCase<RegisterActivity> {

    public static final String USERNAME = "username";
    public static final String DISPLAY_NAME = "displayName";
    public static final String PASSWORD = "password";

    private RegistrationPresenterImpl<ExampleUser> mPresenter;

    @Override
    protected void beforeActivityLaunched() {
        //noinspection unchecked
        mPresenter = mock(RegistrationPresenterImpl.class);
        Injector.register(RegisterActivity.class, new RegisterActivity.DefaultConfiguration() {

            @Override
            protected RegistrationPresenterImpl<ExampleUser> createRegistrationPresenter(@NonNull RegistrationView view, @NonNull RegistrationFlowListener flowListener) {
                return mPresenter;
            }
        });
    }

    @Override
    protected Class<RegisterActivity> getActivityClass() {
        return RegisterActivity.class;
    }

    @Test
    public void registerWithEmptyData() {
        // When
        onView(withId(R.id.register_register_button)).perform(click());

        // Then
        verify(mPresenter).onRegister("", "", "");
    }

    @Test
    public void gotoLoginScreen() {
        // When
        onView(withId(R.id.register_already_registered_button)).perform(click());

        // Then
        verify(mPresenter).onAlreadyRegistered();
    }

    @Test
    public void registerWithAllDetails() {
        // Given
        onView(withId(R.id.register_username_inner)).perform(ViewActions.typeText(USERNAME));
        onView(withId(R.id.register_displayName_inner)).perform(ViewActions.typeText(DISPLAY_NAME));
        onView(withId(R.id.register_password_inner)).perform(ViewActions.typeText(PASSWORD));

        // When
        closeSoftKeyboard();
        onView(withId(R.id.register_register_button)).perform(click());
        verify(mPresenter).onRegister(USERNAME, DISPLAY_NAME, PASSWORD);
    }

}
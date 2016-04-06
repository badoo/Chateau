package com.badoo.chateau.example.ui.conversations.create.selectusers;

import android.support.test.runner.AndroidJUnit4;

import com.badoo.chateau.example.BaseTestCase;
import com.badoo.chateau.example.R;
import com.badoo.chateau.data.models.BaseUser;
import com.badoo.chateau.example.ui.Injector;
import com.badoo.chateau.ui.conversations.create.selectusers.UserListPresenter;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class SelectUserActivityTest extends BaseTestCase<SelectUserActivity> {

    private UserListPresenter mPresenter;
    private UserListPresenter.UserListView mView;

    @Override
    protected Class<SelectUserActivity> getActivityClass() {
        return SelectUserActivity.class;
    }

    @Override
    protected void beforeActivityLaunched() {
        mPresenter = mock(UserListPresenter.class);
        Injector.register(SelectUserActivity.class, new SelectUserActivity.DefaultConfiguration() {

            @Override
            protected UserListPresenter.UserListView createView(SelectUserActivity activity) {
                mView = super.createView(activity);
                return mView;
            }

            @Override
            protected UserListPresenter createPresenter() {
                return mPresenter;
            }
        });
    }

    @Test
    public void startNewConversationWithUser() {
        // Given
        final List<BaseUser> users = createUsers(5);
        runOnUiThread(() -> mView.showUsers(users));

        // When
        onView(withId(R.id.createConversation_userList)).perform(actionOnItemAtPosition(0, click()));

        // Then
        verify(mPresenter).onUsersSelected(users.subList(0, 1));
    }

    @Test
    public void startNewConversationWithMultipleUsers() {
        // Given
        final List<BaseUser> users = createUsers(5);
        runOnUiThread(() -> mView.showUsers(users));

        // When
        onView(withId(R.id.createConversation_groupAction)).perform(click());
        onView(withId(R.id.createConversation_userList)).perform(actionOnItemAtPosition(1, click()));
        onView(withId(R.id.createConversation_userList)).perform(actionOnItemAtPosition(0, click()));
        onView(withId(R.id.createConversation_groupAction)).perform(click());

        // Then
        verify(mPresenter).onUsersSelected(users.subList(0, 2));
    }

    @Test
    public void startNewConversationWithUserAfterExitingMultiUserSelect() {
        // Given
        final List<BaseUser> users = createUsers(5);
        runOnUiThread(() -> mView.showUsers(users));

        // When
        onView(withId(R.id.createConversation_groupAction)).perform(click());
        pressBack();
        onView(withId(R.id.createConversation_userList)).perform(actionOnItemAtPosition(0, click()));

        // Then
        verify(mPresenter).onUsersSelected(users.subList(0, 1));
    }

    @Test
    public void startNewConversationWithMultipleUsersAfterSelectionChange() {
        // Given
        final List<BaseUser> users = createUsers(5);
        runOnUiThread(() -> mView.showUsers(users));

        // When
        onView(withId(R.id.createConversation_groupAction)).perform(click());
        onView(withId(R.id.createConversation_userList)).perform(actionOnItemAtPosition(1, click()));
        onView(withId(R.id.createConversation_userList)).perform(actionOnItemAtPosition(0, click()));
        onView(withId(R.id.createConversation_userList)).perform(actionOnItemAtPosition(2, click()));
        onView(withId(R.id.createConversation_userList)).perform(actionOnItemAtPosition(2, click()));
        onView(withId(R.id.createConversation_groupAction)).perform(click());

        // Then
        verify(mPresenter).onUsersSelected(users.subList(0, 2));
    }

    private List<BaseUser> createUsers(int count) {
        List<BaseUser> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            users.add(new BaseUser(Integer.toString(count), "User " + i));
        }
        return users;
    }
}
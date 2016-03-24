package com.badoo.chateau.example.ui.conversations.list;

import android.support.test.runner.AndroidJUnit4;

import com.badoo.chateau.example.BaseTestCase;
import com.badoo.chateau.example.R;
import com.badoo.chateau.data.models.BaseConversation;
import com.badoo.chateau.example.ui.Injector;
import com.badoo.chateau.ui.conversations.list.ConversationListPresenter;
import com.badoo.chateau.ui.conversations.list.ConversationListView;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class ConversationListActivityTest extends BaseTestCase<ConversationListActivity> {

    private ConversationListPresenter mPresenter;
    private ConversationListView mView;

    @Override
    protected Class<ConversationListActivity> getActivityClass() {
        return ConversationListActivity.class;
    }

    @Override
    protected void beforeActivityLaunched() {
        mPresenter = mock(ConversationListPresenter.class);
        Injector.register(ConversationListActivity.class, new ConversationListActivity.DefaultConfiguration() {

            @Override
            protected ConversationListView createView(ConversationListActivity activity) {
                mView = super.createView(activity);
                return mView;
            }

            @Override
            protected ConversationListPresenter createPresenter() {
                return mPresenter;
            }
        });
    }

    @Test
    public void startNewConversation() {
        // When
        onView(withId(R.id.conversations_start_new_chat_button)).perform(click());

        // Then
        verify(mPresenter).onCreateNewConversationClicked();
    }

    @Test
    public void openConversation() {
        // Given
        List<BaseConversation> conversations = createConversations(5);
        runOnUiThread(() -> mView.showConversations(conversations));

        // When
        onView(withId(R.id.conversations_list)).perform(actionOnItemAtPosition(0, click()));

        // Then
        verify(mPresenter).onConversationClicked(conversations.get(0));
    }

    @Test
    public void deleteSingleConversation() {
        // Given
        List<BaseConversation> conversations = createConversations(5);
        runOnUiThread(() -> mView.showConversations(conversations));

        // When
        onView(withId(R.id.conversations_list)).perform(actionOnItemAtPosition(0, longClick()));
        onView(withId(R.id.action_delete)).perform(click());

        // Then
        verify(mPresenter).onDeleteConversations(Collections.singletonList(conversations.get(0)));
    }

    @Test
    public void deleteMultipleConversations() {
        // Given
        List<BaseConversation> conversations = createConversations(5);
        runOnUiThread(() -> mView.showConversations(conversations));

        // When
        onView(withId(R.id.conversations_list)).perform(actionOnItemAtPosition(0, longClick()));
        onView(withId(R.id.conversations_list)).perform(actionOnItemAtPosition(2, click()));
        onView(withId(R.id.action_delete)).perform(click());

        // Then
        List<BaseConversation> expected = new ArrayList<>();
        expected.add(conversations.get(0));
        expected.add(conversations.get(2));
        verify(mPresenter).onDeleteConversations(expected);
    }

    @Test
    public void backPressCancelsSelection() {
        // Given
        List<BaseConversation> conversations = createConversations(5);
        runOnUiThread(() -> mView.showConversations(conversations));

        // When
        onView(withId(R.id.conversations_list)).perform(actionOnItemAtPosition(0, longClick())); // Enter selection mode
        pressBack();
        onView(withId(R.id.conversations_list)).perform(actionOnItemAtPosition(2, click())); // This should now open the conversation

        // Then
        verify(mPresenter).onConversationClicked(conversations.get(2));
    }

    @Test
    public void unselectingOnlySelectedItemCancelsSelection() {
        // Given
        List<BaseConversation> conversations = createConversations(5);
        runOnUiThread(() -> mView.showConversations(conversations));

        // When
        onView(withId(R.id.conversations_list)).perform(actionOnItemAtPosition(1, longClick())); // Enter selection mode
        onView(withId(R.id.conversations_list)).perform(actionOnItemAtPosition(1, click())); // This should cancel selection mode
        onView(withId(R.id.conversations_list)).perform(actionOnItemAtPosition(1, click())); // This should now open the conversation

        // Then
        verify(mPresenter).onConversationClicked(conversations.get(1));
    }


    private List<BaseConversation> createConversations(int count) {
        List<BaseConversation> conversations = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            conversations.add(new BaseConversation(Integer.toString(i), Integer.toString(i), Collections.emptyList(), null, 0));
        }
        return conversations;
    }
}
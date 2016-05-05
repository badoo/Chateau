package com.badoo.chateau.example.ui.conversations.list;

import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import com.badoo.chateau.example.BaseTestCase;
import com.badoo.chateau.example.R;
import com.badoo.chateau.example.data.model.ExampleConversation;
import com.badoo.chateau.example.ui.Injector;
import com.badoo.chateau.example.ui.conversations.list.CreateConversationPresenter.CreateConversationFlowListener;
import com.badoo.chateau.example.ui.conversations.list.CreateConversationPresenter.CreateConversationView;
import com.badoo.chateau.ui.conversations.list.ConversationListPresenter;
import com.badoo.chateau.ui.conversations.list.ConversationListPresenter.ConversationListFlowListener;
import com.badoo.chateau.ui.conversations.list.ConversationListPresenter.ConversationListView;

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
import static com.badoo.chateau.example.ui.conversations.list.ConversationListActivity.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class ConversationListActivityTest extends BaseTestCase<ConversationListActivity> {

    private ConversationListPresenter<ExampleConversation> mListPresenter;
    private ExampleConversationListView mListView;
    private CreateConversationPresenter mCreateConversationPresenter;

    @Override
    protected Class<ConversationListActivity> getActivityClass() {
        return ConversationListActivity.class;
    }

    @Override
    protected void beforeActivityLaunched() {
        mListPresenter = mock(ConversationListPresenter.class);
        mCreateConversationPresenter = mock(CreateConversationPresenter.class);
        Injector.register(ConversationListActivity.class, new DefaultConfiguration() {

            @Override
            protected ExampleConversationListView createConversationListView(@NonNull ConversationListActivity activity) {
                mListView = super.createConversationListView(activity);
                return mListView;
            }

            @Override
            protected ConversationListPresenter<ExampleConversation> createConversationListPresenter(@NonNull ConversationListView<ExampleConversation> view, @NonNull ConversationListFlowListener<ExampleConversation> flowListener) {
                return mListPresenter;
            }

            @Override
            protected CreateConversationPresenter createCreateConversationPresenter(@NonNull CreateConversationView view, @NonNull CreateConversationFlowListener flowListener) {
                return mCreateConversationPresenter;
            }
        });
    }

    @Test
    public void startNewConversation() {
        // When
        onView(withId(R.id.conversations_start_new_chat_button)).perform(click());

        // Then
        verify(mCreateConversationPresenter).onCreateNewConversationClicked();
    }

    @Test
    public void openConversation() {
        // Given
        List<ExampleConversation> conversations = createConversations(5);
        runOnUiThread(() -> mListView.showConversations(conversations));

        // When
        onView(withId(R.id.conversations_list)).perform(actionOnItemAtPosition(0, click()));

        // Then
        verify(mListPresenter).onConversationClicked(conversations.get(0));
    }

    @Test
    public void deleteSingleConversation() {
        // Given
        List<ExampleConversation> conversations = createConversations(5);
        runOnUiThread(() -> mListView.showConversations(conversations));

        // When
        onView(withId(R.id.conversations_list)).perform(actionOnItemAtPosition(0, longClick()));
        onView(withId(R.id.action_delete)).perform(click());

        // Then
        verify(mListPresenter).onDeleteConversations(Collections.singletonList(conversations.get(0)));
    }

    @Test
    public void deleteMultipleConversations() {
        // Given
        List<ExampleConversation> conversations = createConversations(5);
        runOnUiThread(() -> mListView.showConversations(conversations));

        // When
        onView(withId(R.id.conversations_list)).perform(actionOnItemAtPosition(0, longClick()));
        onView(withId(R.id.conversations_list)).perform(actionOnItemAtPosition(2, click()));
        onView(withId(R.id.action_delete)).perform(click());

        // Then
        List<ExampleConversation> expected = new ArrayList<>();
        expected.add(conversations.get(0));
        expected.add(conversations.get(2));
        verify(mListPresenter).onDeleteConversations(expected);
    }

    @Test
    public void backPressCancelsSelection() {
        // Given
        List<ExampleConversation> conversations = createConversations(5);
        runOnUiThread(() -> mListView.showConversations(conversations));

        // When
        onView(withId(R.id.conversations_list)).perform(actionOnItemAtPosition(0, longClick())); // Enter selection mode
        pressBack();
        onView(withId(R.id.conversations_list)).perform(actionOnItemAtPosition(2, click())); // This should now open the conversation

        // Then
        verify(mListPresenter).onConversationClicked(conversations.get(2));
    }

    @Test
    public void unselectingOnlySelectedItemCancelsSelection() {
        // Given
        List<ExampleConversation> conversations = createConversations(5);
        runOnUiThread(() -> mListView.showConversations(conversations));

        // When
        onView(withId(R.id.conversations_list)).perform(actionOnItemAtPosition(1, longClick())); // Enter selection mode
        onView(withId(R.id.conversations_list)).perform(actionOnItemAtPosition(1, click())); // This should cancel selection mode
        onView(withId(R.id.conversations_list)).perform(actionOnItemAtPosition(1, click())); // This should now open the conversation

        // Then
        verify(mListPresenter).onConversationClicked(conversations.get(1));
    }


    private List<ExampleConversation> createConversations(int count) {
        List<ExampleConversation> conversations = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            conversations.add(new ExampleConversation(Integer.toString(i), Integer.toString(i), Collections.emptyList(), null, 0));
        }
        return conversations;
    }
}
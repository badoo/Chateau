package com.badoo.chateau.example.ui.chat;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.badoo.chateau.data.models.payloads.ImagePayload;
import com.badoo.chateau.data.models.payloads.Payload;
import com.badoo.chateau.data.models.payloads.TextPayload;
import com.badoo.chateau.example.BaseTestCase;
import com.badoo.chateau.example.R;
import com.badoo.chateau.example.data.model.ExampleConversation;
import com.badoo.chateau.example.data.model.ExampleMessage;
import com.badoo.chateau.example.ui.Injector;
import com.badoo.chateau.example.ui.chat.messages.ExampleMessageListView;
import com.badoo.chateau.ui.chat.input.ChatInputPresenter;
import com.badoo.chateau.ui.chat.messages.MessageListPresenter;
import com.badoo.chateau.ui.chat.messages.MessageListPresenter.MessageListFlowListener;
import com.badoo.chateau.ui.chat.messages.MessageListPresenter.MessageListView;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.registerIdlingResources;
import static android.support.test.espresso.Espresso.unregisterIdlingResources;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.badoo.chateau.ui.chat.input.ChatInputPresenter.ChatInputFlowListener;
import static com.badoo.chateau.ui.chat.input.ChatInputPresenter.ChatInputView;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class ChatActivityTest extends BaseTestCase<ChatActivity> {

    private static final String CHAT_ID = "chatId";
    private static final String CHAT_NAME = "chatName";
    private static final String MESSAGE = "message";

    private MessageListPresenter mListPresenter;
    private ChatInputPresenter mInputPresenter;
    private ExampleMessageListView mMessageListView;

    @Override
    protected Class<ChatActivity> getActivityClass() {
        return ChatActivity.class;
    }

    @Override
    protected Intent getActivityIntent() {
        return ChatActivity.create(InstrumentationRegistry.getContext(), CHAT_ID, CHAT_NAME);
    }

    @Override
    protected void beforeActivityLaunched() {
        mListPresenter = mock(MessageListPresenter.class);
        mInputPresenter = mock(ChatInputPresenter.class);
        Injector.register(ChatActivity.class, new ChatActivity.DefaultConfiguration() {

            @Override
            protected ExampleMessageListView createMessageListView(@NonNull ChatActivity activity, @NonNull String chatId) {
                mMessageListView = super.createMessageListView(activity, chatId);
                return mMessageListView;
            }

            @Override
            protected MessageListPresenter createMessageListPresenter(@NonNull MessageListView<ExampleMessage, ExampleConversation> view, @NonNull MessageListFlowListener flowListener, @NonNull String chatId) {
                return mListPresenter;
            }

            @Override
            protected ChatInputPresenter createChatInputPresenter(@NonNull ChatInputView view, @NonNull ChatInputFlowListener flowListener, @NonNull String chatId) {
                return mInputPresenter;
            }
        });
    }

    @Test
    public void userIsTyping() {
        // When
        onView(withId(R.id.chatTextInput_editText)).perform(typeText(MESSAGE));

        // Then
        verify(mInputPresenter).onUserTyping();
    }

    @Test
    public void sendMessage() {
        // When
        onView(withId(R.id.chatTextInput_editText)).perform(typeText(MESSAGE));
        onView(withId(R.id.chatTextInput_sendEnabled)).perform(click());

        // Then
        verify(mInputPresenter).onSendMessage(MESSAGE);
    }

    @Test
    public void startSendingGalleryImage() {
        // When
        onView(withId(R.id.action_attachPhoto)).perform(click());

        // Then
        verify(mInputPresenter).onPickImage();
    }

    @Test
    public void startTakePhoto() {
        // When
        onView(withId(R.id.action_takePhoto)).perform(click());

        // Then
        verify(mInputPresenter).onTakePhoto();
    }

    @Test
    public void clickOnImageMessage() {
        // Given
        final List<ExampleMessage> messages = createPhotoMessages(5);
        runOnUiThread(() -> mMessageListView.showMessages(messages));

        // Then
        onView(withId(R.id.chat_list)).perform(actionOnItemAtPosition(4, click()));

        // Then
        Uri uri = Uri.parse(((ImagePayload) messages.get(4).getPayload()).getImageUrl());
        verify(mListPresenter).onImageClicked(uri);
    }

    @Test
    public void testLoadMoreWhenScrollToTop() {
        // Given
        final List<ExampleMessage> messages = createTextMessages(40);
        runOnUiThread(() -> mMessageListView.showMessages(messages));

        // When
        onView(withId(R.id.chat_list)).perform(scrollToPosition(0));
        // Need to wait for the scroll to complete before we verify if more data was requested
        final WaitForRecycleViewScrollIdlingResource<ChatActivity> idlingResource = new WaitForRecycleViewScrollIdlingResource<>(R.id.chat_list, 0, mActivityRule);
        try {
            registerIdlingResources(idlingResource);

            // Then
            verify(mListPresenter).onMoreMessagesRequired();
        }
        finally {
            unregisterIdlingResources(idlingResource);
        }
    }

    private List<ExampleMessage> createTextMessages(int count) {
        List<ExampleMessage> messages = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String id = Integer.toString(i);
            Payload payload = new TextPayload("msg-" + i);
            messages.add(new ExampleMessage(id, "", false, "sender", payload, i, false));
        }
        return messages;
    }


    private List<ExampleMessage> createPhotoMessages(int count) {
        List<ExampleMessage> messages = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String id = Integer.toString(i);
            Payload payload = new ImagePayload("http://www.badoo.com/broken.png", null, "msg-" + i);
            messages.add(new ExampleMessage(id, "", false, "sender", payload, i, false));
        }
        return messages;
    }


}
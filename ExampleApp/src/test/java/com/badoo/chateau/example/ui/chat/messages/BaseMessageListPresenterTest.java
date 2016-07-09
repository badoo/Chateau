package com.badoo.chateau.example.ui.chat.messages;

import android.support.annotation.NonNull;

import com.badoo.chateau.core.model.Conversation;
import com.badoo.chateau.core.model.Message;
import com.badoo.chateau.core.repos.messages.MessageDataSource;
import com.badoo.chateau.core.repos.messages.MessageDataSource.LoadResult;
import com.badoo.chateau.core.repos.messages.MessageDataSource.Update;
import com.badoo.chateau.core.usecases.conversations.GetConversation;
import com.badoo.chateau.core.usecases.conversations.MarkConversationRead;
import com.badoo.chateau.core.usecases.istyping.SubscribeToUsersTyping;
import com.badoo.chateau.core.usecases.messages.LoadMessages;
import com.badoo.chateau.core.usecases.messages.SendMessage;
import com.badoo.chateau.core.usecases.messages.SubscribeToMessageUpdates;
import com.badoo.chateau.example.data.model.ExampleUser;
import com.badoo.chateau.ui.chat.messages.BaseMessageListPresenter;
import com.badoo.chateau.ui.chat.messages.MessageListPresenter;
import com.badoo.chateau.ui.chat.messages.MessageListPresenter.MessageListView;
import com.badoo.unittest.rx.BaseRxTestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BaseMessageListPresenterTest extends BaseRxTestCase {

    private static final String CONVERSATION_ID = "chatId";

    @Mock
    private MessageListView<TestMessage> mView;
    @Mock
    private LoadMessages<TestMessage> mLoadMessages;
    @Mock
    private SubscribeToMessageUpdates<TestMessage> mSubscribeToMessageUpdates;
    @Mock
    private MarkConversationRead mMarkConversationRead;
    @Mock
    private GetConversation<TestConversation> mGetConversation;
    @Mock
    private SubscribeToUsersTyping<ExampleUser> mSubscribeToUsersTyping;
    @Mock
    private SendMessage<TestMessage> mSendMessage;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void noUseCasesInvokedFromConstructor() {
        verifyZeroInteractions(mLoadMessages);
        verifyZeroInteractions(mSubscribeToMessageUpdates);
        verifyZeroInteractions(mMarkConversationRead);
        verifyZeroInteractions(mGetConversation);
        verifyZeroInteractions(mSubscribeToUsersTyping);
    }

    @Test
    public void messagesRequestedWhenPresenterCreated() {
        // Given
        when(mGetConversation.execute(any())).thenReturn(Observable.just(new TestConversation()));
        when(mLoadMessages.all(eq(CONVERSATION_ID))).thenReturn(Observable.empty());
        when(mSubscribeToMessageUpdates.forConversation(any())).thenReturn(Observable.empty());
        when(mSubscribeToUsersTyping.execute(any())).thenReturn(Observable.empty());
        when(mMarkConversationRead.execute(any())).thenReturn(Observable.empty());

        // When
        MessageListPresenter<TestMessage> target = createPresenter();
        target.onStart();

        // Then
        verify(mLoadMessages).all(eq(CONVERSATION_ID));
    }

    public void viewUpdatedWithMessages() {
        // Given
        when(mGetConversation.execute(any())).thenReturn(Observable.just(new TestConversation()));
        List<TestMessage> messages = Collections.singletonList(new TestMessage());
        LoadResult<TestMessage> loadResult = new LoadResult<>(messages, false, false);
        PublishSubject<Update<TestMessage>> updatePublisher = PublishSubject.create();
        when(mLoadMessages.all(eq(CONVERSATION_ID))).thenAnswer(invocation -> {
            return Observable.just(loadResult);
        });
        when(mSubscribeToMessageUpdates.forConversation(eq(CONVERSATION_ID))).thenReturn(updatePublisher);
        when(mSubscribeToUsersTyping.execute(any())).thenReturn(Observable.empty());
        when(mMarkConversationRead.execute(any())).thenReturn(Observable.empty());

        // When
        MessageListPresenter<TestMessage> target = createPresenter();
        target.onStart();

        // Then
        verify(mView).showMessages(messages);
    }


    @Test
    public void dontRequestMoreMessagesIfConversationEmpty() {
        // Given
        when(mGetConversation.execute(any())).thenReturn(Observable.just(new TestConversation()));
        Observable<LoadResult<TestMessage>> noMessages = Observable.just(new LoadResult<>(Collections.emptyList(), false, false));
        when(mLoadMessages.all(eq(CONVERSATION_ID))).thenReturn(noMessages);
        when(mSubscribeToMessageUpdates.forConversation(any())).thenReturn(Observable.empty());
        when(mSubscribeToUsersTyping.execute(any())).thenReturn(Observable.empty());
        when(mMarkConversationRead.execute(any())).thenReturn(Observable.empty());

        // When
        MessageListPresenter<TestMessage> target = createPresenter();
        target.onStart();
        target.onMoreMessagesRequired();


        // Then
        verify(mLoadMessages, times(1)).all(eq(CONVERSATION_ID)); // Requested once in onCreate
    }

    @NonNull
    private MessageListPresenter<TestMessage> createPresenter() {
        return new BaseMessageListPresenter<>(CONVERSATION_ID, mView, mLoadMessages, mSubscribeToMessageUpdates, mMarkConversationRead, mSendMessage);
    }

    private static class TestMessage implements Message {
    }

    private static class TestConversation implements Conversation {
    }

}
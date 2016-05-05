package com.badoo.chateau.example.ui.chat.messages;

import com.badoo.chateau.core.usecases.conversations.GetConversation;
import com.badoo.chateau.core.usecases.conversations.MarkConversationRead;
import com.badoo.chateau.core.usecases.istyping.SubscribeToUsersTyping;
import com.badoo.chateau.core.usecases.messages.LoadMessages;
import com.badoo.chateau.core.usecases.messages.SubscribeToMessages;
import com.badoo.chateau.example.data.model.ExampleConversation;
import com.badoo.chateau.example.data.model.ExampleMessage;
import com.badoo.chateau.example.data.model.ExampleUser;
import com.badoo.chateau.ui.chat.messages.BaseMessageListPresenter;
import com.badoo.chateau.ui.chat.messages.MessageListPresenter;
import com.badoo.chateau.ui.chat.messages.MessageListPresenter.MessageListFlowListener;
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
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BaseMessageListPresenterTest extends BaseRxTestCase {

    private static final String CHAT_ID = "chatId";

    private MessageListPresenter mTarget;

    @Mock
    private ExampleMessageListView mView;
    @Mock
    private MessageListFlowListener mFlowListener;
    @Mock
    private LoadMessages<ExampleMessage> mLoadMessages;
    @Mock
    private SubscribeToMessages<ExampleMessage> mSubscribeToMessages;
    @Mock
    private MarkConversationRead mMarkConversationRead;
    @Mock
    private GetConversation<ExampleConversation> mGetConversation;
    @Mock
    private SubscribeToUsersTyping<ExampleUser> mSubscribeToUsersTyping;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mTarget = new BaseMessageListPresenter<>(CHAT_ID, mView, mFlowListener, mLoadMessages, mSubscribeToMessages, mMarkConversationRead, mGetConversation, mSubscribeToUsersTyping);
    }

    @Test
    public void noUseCasesInvokedFromConstructor() {
        verifyZeroInteractions(mLoadMessages);
        verifyZeroInteractions(mSubscribeToMessages);
        verifyZeroInteractions(mMarkConversationRead);
        verifyZeroInteractions(mGetConversation);
        verifyZeroInteractions(mSubscribeToUsersTyping);
    }

    @Test
    public void messagesRequestedWhenPresenterCreated() {
        // Given
        when(mGetConversation.execute(any())).thenReturn(Observable.just(new ExampleConversation("id", "name", Collections.emptyList(), null, 0)));
        when(mLoadMessages.execute(eq(CHAT_ID), any())).thenReturn(Observable.empty());
        when(mSubscribeToMessages.execute(any())).thenReturn(Observable.empty());
        when(mSubscribeToUsersTyping.execute(any())).thenReturn(Observable.empty());
        when(mMarkConversationRead.execute(any())).thenReturn(Observable.empty());

        // When
        mTarget.onCreate();

        // Then
        verify(mLoadMessages).execute(eq(CHAT_ID), any());
    }

    @Test
    public void viewUpdatedWithMessages() {
        // Given
        when(mGetConversation.execute(any())).thenReturn(Observable.just(new ExampleConversation("id", "name", Collections.emptyList(), null, 0)));
        List<ExampleMessage> messages = Collections.singletonList(ExampleMessage.createTimestamp(0));
        PublishSubject<List<ExampleMessage>> messagePublisher = PublishSubject.create();
        when(mLoadMessages.execute(eq(CHAT_ID), isNull(ExampleMessage.class))).thenAnswer(invocation -> {
            messagePublisher.onNext(messages);
            return Observable.just(true);
        });
        when(mSubscribeToMessages.execute(eq(CHAT_ID))).thenReturn(messagePublisher);
        when(mSubscribeToUsersTyping.execute(any())).thenReturn(Observable.empty());
        when(mMarkConversationRead.execute(any())).thenReturn(Observable.empty());

        // When
        mTarget.onCreate();

        // Then
        verify(mView).showMessages(messages);
    }
// TODO: These tests are currently broken due to a scheduler issue.  Need to be reexamined and fixed.
//    @Test
//    public void requestMoreMessagesPassesPreviousOldestMessage() {
//        // Given
//        when(mGetConversation.execute(any())).thenReturn(Observable.just(new ExampleConversation("id", "name", Collections.emptyList(), null, 0)));
//        final List<ExampleMessage> messages = Collections.singletonList(ExampleMessage.createTimestamp(0));
//        final PublishSubject<List<ExampleMessage>> messagePublisher = PublishSubject.create();
//        when(mLoadMessages.execute(eq(CHAT_ID), isNull(ExampleMessage.class))).thenAnswer(invocation -> {
//            messagePublisher.onNext(messages);
//            return Observable.just(true);
//        });
//        when(mSubscribeToMessages.execute(any())).thenReturn(messagePublisher);
//        when(mSubscribeToUsersTyping.execute(any())).thenReturn(Observable.empty());
//        when(mMarkConversationRead.execute(any())).thenReturn(Observable.empty());
//
//        // When
//        mTarget.onCreate();
//        mTarget.onMoreMessagesRequired();
//
//
//        // Then
//        InOrder inOrder = inOrder(mLoadMessages, mLoadMessages);
//        inOrder.verify(mLoadMessages).execute(CHAT_ID, null);
//        inOrder.verify(mLoadMessages).execute(CHAT_ID, messages.get(0));
//    }
//
//    @Test
//    public void dontRequestMoreMessagesIfConversationEmpty() {
//        // Given
//        when(mGetConversation.execute(any())).thenReturn(Observable.just(new ExampleConversation("id", "name", Collections.emptyList(), null, 0)));
//        when(mLoadMessages.execute(eq(CHAT_ID), isNull(ExampleMessage.class))).thenReturn(Observable.empty());
//        when(mSubscribeToMessages.execute(any())).thenReturn(Observable.empty());
//        when(mSubscribeToUsersTyping.execute(any())).thenReturn(Observable.empty());
//        when(mMarkConversationRead.execute(any())).thenReturn(Observable.empty());
//
//        // When
//        mTarget.onCreate();
//        mTarget.onMoreMessagesRequired();
//
//
//        // Then
//        verify(mLoadMessages, times(1)).execute(eq(CHAT_ID), isNull(ExampleMessage.class)); // Requested once in onCreate
//    }


}
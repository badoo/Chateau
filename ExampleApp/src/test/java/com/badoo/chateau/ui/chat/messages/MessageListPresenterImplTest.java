package com.badoo.chateau.ui.chat.messages;

import com.badoo.chateau.core.usecases.conversations.GetConversation;
import com.badoo.chateau.core.usecases.conversations.MarkConversationRead;
import com.badoo.chateau.core.usecases.istyping.SubscribeToUsersTyping;
import com.badoo.chateau.core.usecases.messages.GetChatMessages;
import com.badoo.chateau.core.usecases.messages.SubscribeToNewMessages;
import com.badoo.chateau.core.usecases.messages.SubscribeToUpdatedMessages;
import com.badoo.chateau.data.models.BaseConversation;
import com.badoo.chateau.data.models.BaseMessage;
import com.badoo.unittest.rx.BaseRxTestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MessageListPresenterImplTest extends BaseRxTestCase {

    private static final String CHAT_ID = "chatId";

    private MessageListPresenter mTarget;
    @Mock private GetChatMessages mGetChatMessages;
    @Mock private SubscribeToNewMessages mSubscribeToNewMessages;
    @Mock private SubscribeToUpdatedMessages mSubscribeToUpdatedMessages;
    @Mock private MarkConversationRead mMarkConversationRead;
    @Mock private GetConversation mGetConversation;
    @Mock private SubscribeToUsersTyping mSubscribeToUsersTyping;
    @Mock private MessageListView mView;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mTarget = new MessageListPresenterImpl(CHAT_ID, mGetChatMessages, mSubscribeToNewMessages, mSubscribeToUpdatedMessages, mMarkConversationRead, mGetConversation, mSubscribeToUsersTyping);
        mTarget.attachView(mView);
    }

    @Test
    public void noUseCasesInvokedFromConstructor() {
        verifyZeroInteractions(mGetChatMessages);
        verifyZeroInteractions(mSubscribeToNewMessages);
        verifyZeroInteractions(mSubscribeToUpdatedMessages);
        verifyZeroInteractions(mMarkConversationRead);
        verifyZeroInteractions(mGetConversation);
        verifyZeroInteractions(mSubscribeToUsersTyping);
    }

    @Test
    public void messagesRequestedWhenPresenterCreated() {
        // Given
        when(mGetConversation.execute(any())).thenReturn(Observable.just(new BaseConversation("id", "name", Collections.emptyList(), null, 0)));
        when(mGetChatMessages.execute(any())).thenReturn(Observable.empty());
        when(mSubscribeToNewMessages.execute(any())).thenReturn(Observable.just(BaseMessage.createTimestamp(0)));
        when(mSubscribeToUpdatedMessages.execute(any())).thenReturn(Observable.empty());
        when(mSubscribeToUsersTyping.execute(any())).thenReturn(Observable.empty());
        when(mMarkConversationRead.execute(any())).thenReturn(Observable.empty());

        // When
        mTarget.onCreate();

        // Then
        verify(mGetChatMessages).execute(new GetChatMessages.GetChatMessagesParams(CHAT_ID));
    }

    @Test
    public void viewUpdatedWithMessages() {
        // Given
        when(mGetConversation.execute(any())).thenReturn(Observable.just(new BaseConversation("id", "name", Collections.emptyList(), null, 0)));
        List<BaseMessage> messages = Collections.singletonList(BaseMessage.createTimestamp(0));
        when(mGetChatMessages.execute(any())).thenReturn(Observable.just(new ArrayList<>(messages)));
        when(mSubscribeToNewMessages.execute(any())).thenReturn(Observable.empty());
        when(mSubscribeToUpdatedMessages.execute(any())).thenReturn(Observable.empty());
        when(mSubscribeToUsersTyping.execute(any())).thenReturn(Observable.empty());
        when(mMarkConversationRead.execute(any())).thenReturn(Observable.empty());

        // When
        mTarget.onCreate();

        // Then
        verify(mView).showMessages(messages);
    }

    @Test
    public void requestMoreMessagesPassesPreviousOldestMessage() {
        // Given
        when(mGetConversation.execute(any())).thenReturn(Observable.just(new BaseConversation("id", "name", Collections.emptyList(), null, 0)));
        List<BaseMessage> messages = Collections.singletonList(BaseMessage.createTimestamp(0));
        when(mGetChatMessages.execute(any())).thenReturn(Observable.just(new ArrayList<>(messages)));
        when(mSubscribeToNewMessages.execute(any())).thenReturn(Observable.empty());
        when(mSubscribeToUpdatedMessages.execute(any())).thenReturn(Observable.empty());
        when(mSubscribeToUsersTyping.execute(any())).thenReturn(Observable.empty());
        when(mMarkConversationRead.execute(any())).thenReturn(Observable.empty());

        // When
        mTarget.onCreate();
        mTarget.onMoreMessagesRequired();


        // Then
        verify(mGetChatMessages).execute(new GetChatMessages.GetChatMessagesParams("id", messages.get(0)));
    }

    @Test
    public void dontRequestMoreMessagesIfConversationEmpty() {
        // Given
        when(mGetConversation.execute(any())).thenReturn(Observable.just(new BaseConversation("id", "name", Collections.emptyList(), null, 0)));
        when(mGetChatMessages.execute(any())).thenReturn(Observable.empty());
        when(mSubscribeToNewMessages.execute(any())).thenReturn(Observable.empty());
        when(mSubscribeToUpdatedMessages.execute(any())).thenReturn(Observable.empty());
        when(mSubscribeToUsersTyping.execute(any())).thenReturn(Observable.empty());
        when(mMarkConversationRead.execute(any())).thenReturn(Observable.empty());

        // When
        mTarget.onCreate();
        mTarget.onMoreMessagesRequired();


        // Then
        verify(mGetChatMessages, times(1)).execute(any()); // Requested once in onCreate
    }


}
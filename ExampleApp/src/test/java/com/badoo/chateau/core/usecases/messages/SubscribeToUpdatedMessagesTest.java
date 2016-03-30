package com.badoo.chateau.core.usecases.messages;

import com.badoo.chateau.core.model.Message;
import com.badoo.chateau.core.repos.messages.MessageQuery;
import com.badoo.chateau.core.repos.messages.MessageRepository;
import com.badoo.unittest.rx.BaseRxTestCase;

import org.junit.Before;
import org.junit.Test;

import rx.Observable;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SubscribeToUpdatedMessagesTest extends BaseRxTestCase {

    private MessageRepository mMockRepository;
    private SubscribeToUpdatedMessages mTarget;

    @Before
    public void beforeTest() {
        super.beforeTest();
        mMockRepository = mock(MessageRepository.class);
        mTarget = new SubscribeToUpdatedMessages(mMockRepository);
    }

    @Test
    public void whenMessagesForChatRequested_thenRepoIsQueriedForCorrectChatId() throws Exception {
        // Setup
        final String chatId = "chatId";
        final Message expectedResult = new Message() {};
        when(mMockRepository.query(eq(new MessageQuery.GetUpdatedMessagesForConversation(chatId))))
            .thenReturn(Observable.just(expectedResult));

        // Execute
        mTarget.execute(new ChatParams(chatId));

        // Assert
        verify(mMockRepository, times(1)).query(eq(new MessageQuery.GetUpdatedMessagesForConversation(chatId)));
    }

    @Test
    public void thatResultIsReturnedOnMainThread() throws Exception {
        // Setup
        final String chatId = "chatId";
        final Message expectedResult = new Message() {};

        when(mMockRepository.query(eq(new MessageQuery.GetUpdatedMessagesForConversation(chatId))))
            .thenReturn(Observable.just(expectedResult));

        // Execute & Assert
        mTarget.execute( new ChatParams(chatId))
            .doOnNext(__ -> assertTrue("Result not returned on main thread", getSchedulerFactory().isOnMainScheduler(Thread.currentThread())))
            .subscribe();
    }

}
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

public class SendMessagesTest extends BaseRxTestCase {

    private MessageRepository mMockRepository;
    private SendMessage mTarget;

    @Before
    public void beforeTest() {
        super.beforeTest();
        mMockRepository = mock(MessageRepository.class);
        mTarget = new SendMessage(mMockRepository);
    }

    @Test
    public void whenMessagesForChatSent_thenRepoIsQueriedWithSentMessage() throws Exception {
        // Setup
        final String chatId = "chatId";
        final Message message = new Message() {};
        when(mMockRepository.query(eq(new MessageQuery.SendMessage(chatId, message))))
            .thenReturn(Observable.never());

        // Execute
        mTarget.execute(new SendMessage.SendMessageParams(chatId, message));

        // Assert
        verify(mMockRepository, times(1)).query(eq(new MessageQuery.SendMessage(chatId, message)));
    }

    @Test
    public void thatResultIsReturnedOnMainThread() throws Exception {
        // Setup
        final String chatId = "chatId";
        final Message message = new Message() {};
        when(mMockRepository.query(eq(new MessageQuery.SendMessage(chatId, message))))
            .thenReturn(Observable.never());

        // Execute & Assert
        mTarget.execute(new SendMessage.SendMessageParams(chatId, message))
            .doOnNext(__ -> assertTrue("Result not returned on main thread", getSchedulerFactory().isOnMainScheduler(Thread.currentThread())))
            .subscribe();
    }

}
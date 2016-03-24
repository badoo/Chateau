package com.badoo.chateau.core.usecases.messages;

import com.badoo.chateau.core.model.Message;
import com.badoo.chateau.core.repos.messages.MessageRepository;
import com.badoo.unittest.rx.BaseRxTestCase;

import org.junit.Before;
import org.junit.Test;

import rx.Observable;

import static com.badoo.chateau.core.repos.messages.MessageQuery.GetMessages;
import static com.badoo.chateau.core.usecases.messages.GetChatMessages.GetChatMessagesParams;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetChatMessagesTest extends BaseRxTestCase {

    private MessageRepository mMockRepository;
    private GetChatMessages mTarget;

    @Before
    public void beforeTest() {
        super.beforeTest();
        mMockRepository = mock(MessageRepository.class);
        mTarget = new GetChatMessages(mMockRepository);
    }

    @Test
    public void whenMessagesForChatRequested_thenRepoIsQueriedForCorrectChatId() throws Exception {
        // Setup
        final String chatId = "chatId";
        final Message chunkBefore = new Message() {};
        final Message expectedResult = new Message() {};
        when(mMockRepository.query(eq(new GetMessages(chatId, chunkBefore))))
            .thenReturn(Observable.just(expectedResult));

        // Execute
        mTarget.execute(new GetChatMessagesParams(chatId, chunkBefore));

        // Assert
        verify(mMockRepository, times(1)).query(eq(new GetMessages(chatId, chunkBefore)));
    }

    @Test
    public void thatResultIsReturnedOnMainThread() throws Exception {
        // Setup
        final String chatId = "chatId";
        final Message chunkBefore = new Message() {};
        final Message expectedResult = new Message() {};
        when(mMockRepository.query(eq(new GetMessages(chatId, chunkBefore))))
            .thenReturn(Observable.just(expectedResult));

        // Execute & Assert
        mTarget.execute(new GetChatMessagesParams(chatId, chunkBefore))
            .doOnNext(__ -> assertTrue("Result not returned on main thread", getSchedulerFactory().isOnMainScheduler(Thread.currentThread())))
            .subscribe();
    }

}
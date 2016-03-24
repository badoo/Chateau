package com.badoo.chateau.core.usecases.istyping;

import com.badoo.chateau.core.model.User;
import com.badoo.chateau.core.repos.istyping.IsTypingQuery;
import com.badoo.chateau.core.repos.istyping.IsTypingRepository;
import com.badoo.chateau.core.usecases.istyping.SendUserIsTyping;
import com.badoo.chateau.core.usecases.messages.ChatParams;
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

public class SendUserIsTypingTest extends BaseRxTestCase {

    private static final String CHAT_ID = "chat";

    private IsTypingRepository mMockRepository;
    private SendUserIsTyping mTarget;

    @Before
    public void beforeTest() {
        super.beforeTest();
        mMockRepository = mock(IsTypingRepository.class);
        mTarget = new SendUserIsTyping(mMockRepository);
    }

    @Test
    public void whenMessagesForChatRequested_thenRepoIsQueriedForCorrectChatId() throws Exception {
        // Setup
        User user = mock(User.class);
        when(mMockRepository.query(eq(new IsTypingQuery.SendIsTyping(CHAT_ID))))
            .thenReturn(Observable.empty());

        // Execute
        mTarget.execute(new ChatParams(CHAT_ID));

        // Assert
        verify(mMockRepository, times(1)).query(eq(new IsTypingQuery.SendIsTyping(CHAT_ID)));
    }

    @Test
    public void thatResultIsReturnedOnMainThread() throws Exception {
        // Setup
        User user = mock(User.class);
        when(mMockRepository.query(eq(new IsTypingQuery.SendIsTyping(CHAT_ID))))
            .thenReturn(Observable.empty());

        // Execute & Assert
        mTarget.execute(new ChatParams(CHAT_ID))
            .doOnNext(__ -> assertTrue("Result not returned on main thread", getSchedulerFactory().isOnMainScheduler(Thread.currentThread())))
            .subscribe();
    }

}
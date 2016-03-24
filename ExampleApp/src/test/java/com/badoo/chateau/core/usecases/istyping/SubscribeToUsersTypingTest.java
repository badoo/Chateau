package com.badoo.chateau.core.usecases.istyping;

import com.badoo.chateau.core.model.User;
import com.badoo.chateau.core.repos.istyping.IsTypingQuery;
import com.badoo.chateau.core.repos.istyping.IsTypingRepository;
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

public class SubscribeToUsersTypingTest extends BaseRxTestCase {

    private static final String CHAT_ID = "chat";

    private IsTypingRepository mMockRepository;
    private SubscribeToUsersTyping mTarget;

    @Before
    public void beforeTest() {
        super.beforeTest();
        mMockRepository = mock(IsTypingRepository.class);
        mTarget = new SubscribeToUsersTyping(mMockRepository);
    }

    @Test
    public void whenMessagesForChatRequested_thenRepoIsQueriedForCorrectChatId() throws Exception {
        // Setup
        User user = mock(User.class);
        when(mMockRepository.query(eq(new IsTypingQuery.SubscribeToUsersTyping(CHAT_ID))))
            .thenReturn(Observable.just(user));

        // Execute
        mTarget.execute(new ChatParams(CHAT_ID));

        // Assert
        verify(mMockRepository, times(1)).query(eq(new IsTypingQuery.SubscribeToUsersTyping(CHAT_ID)));
    }

    @Test
    public void thatResultIsReturnedOnMainThread() throws Exception {
        // Setup
        User user = mock(User.class);
        when(mMockRepository.query(eq(new IsTypingQuery.SubscribeToUsersTyping(CHAT_ID))))
            .thenReturn(Observable.just(user));

        // Execute & Assert
        mTarget.execute(new ChatParams(CHAT_ID))
            .doOnNext(__ -> assertTrue("Result not returned on main thread", getSchedulerFactory().isOnMainScheduler(Thread.currentThread())))
            .subscribe();
    }

}
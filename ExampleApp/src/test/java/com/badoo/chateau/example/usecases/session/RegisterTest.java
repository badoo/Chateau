package com.badoo.chateau.example.usecases.session;

import com.badoo.chateau.data.models.BaseUser;
import com.badoo.chateau.data.repos.session.SessionRepository;
import com.badoo.chateau.data.repos.session.SessionRepository.SessionQuery;
import com.badoo.unittest.rx.BaseRxTestCase;

import org.junit.Before;
import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RegisterTest extends BaseRxTestCase {

    private SessionRepository mMockRepository;
    private Register mTarget;

    @Before
    public void beforeTest() {
        super.beforeTest();
        mMockRepository = mock(SessionRepository.class);
        mTarget = new Register(mMockRepository);
    }

    @Test
    public void whenMessagesForChatRequested_thenRepoIsQueriedForCorrectChatId() throws Exception {
        // Setup
        final String userName = "userName";
        final String displayName = "displayName";
        final String password = "password";
        final BaseUser expectedResult = new BaseUser("id", "displayName");
        when(mMockRepository.query(eq(SessionQuery.register(userName, displayName, password))))
            .thenReturn(Observable.just(expectedResult));

        // Execute
        mTarget.execute(new Register.RegisterParams(userName, displayName, password));

        // Assert
        verify(mMockRepository, times(1)).query(eq(SessionQuery.register(userName, displayName, password)));
    }

    @Test
    public void thatResultIsReturnedOnMainThread() throws Exception {
        // Setup
        final String userName = "userName";
        final String displayName = "displayName";
        final String password = "password";
        final BaseUser expectedResult = new BaseUser("id", "displayName");
        when(mMockRepository.query(eq(SessionQuery.register(userName, displayName, password))))
            .thenReturn(Observable.just(expectedResult));

        // Execute
        final TestSubscriber<?> testSubscriber = executeTarget(mTarget.execute(new Register.RegisterParams(userName, displayName, password)));

        // Assert
        assertOnMainThreadScheduler(testSubscriber.getLastSeenThread());
        testSubscriber.assertCompleted();
    }

}
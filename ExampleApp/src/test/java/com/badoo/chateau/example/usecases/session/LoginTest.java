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

public class LoginTest extends BaseRxTestCase {

    private SessionRepository mMockRepository;
    private Login mTarget;

    @Before
    public void beforeTest() {
        super.beforeTest();
        mMockRepository = mock(SessionRepository.class);
        mTarget = new Login(mMockRepository);
    }

    @Test
    public void whenMessagesForChatRequested_thenRepoIsQueriedForCorrectChatId() throws Exception {
        // Setup
        final String userName = "userName";
        final String password = "password";
        final BaseUser expectedResult = new BaseUser("id", "displayName");
        when(mMockRepository.query(eq(SessionQuery.login(userName, password))))
            .thenReturn(Observable.just(expectedResult));

        // Execute
        mTarget.execute(new Login.LoginParams(userName, password));

        // Assert
        verify(mMockRepository, times(1)).query(eq(SessionQuery.login(userName, password)));
    }

    @Test
    public void thatResultIsReturnedOnMainThread() throws Exception {
        // Setup
        final String userName = "userName";
        final String password = "password";
        final BaseUser expectedResult = new BaseUser("id", "displayName");
        when(mMockRepository.query(eq(SessionQuery.login(userName, password))))
            .thenReturn(Observable.just(expectedResult));

        // Execute
        final TestSubscriber<?> testSubscriber = executeTarget(mTarget.execute(new Login.LoginParams(userName, password)));

        // Assert
        assertOnMainThreadScheduler(testSubscriber.getLastSeenThread());
        testSubscriber.assertCompleted();
    }

}
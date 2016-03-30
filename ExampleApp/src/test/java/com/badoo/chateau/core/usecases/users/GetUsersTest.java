package com.badoo.chateau.core.usecases.users;

import com.badoo.barf.usecase.UseCase;
import com.badoo.chateau.core.usecases.users.GetUsers;
import com.badoo.chateau.data.models.BaseUser;
import com.badoo.chateau.core.repos.users.UserQuery;
import com.badoo.chateau.core.repos.users.UserRepository;
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

public class GetUsersTest extends BaseRxTestCase {

    private UserRepository mMockRepository;
    private GetUsers mTarget;

    @Before
    public void beforeTest() {
        super.beforeTest();
        mMockRepository = mock(UserRepository.class);
        mTarget = new GetUsers(mMockRepository);
    }

    @Test
    public void whenMessagesForChatRequested_thenRepoIsQueriedForCorrectChatId() throws Exception {
        // Setup
        final BaseUser expectedResult = new BaseUser("id", "displayName");
        when(mMockRepository.query(eq(new UserQuery.GetAllUsersQuery())))
            .thenReturn(Observable.just(expectedResult));

        // Execute
        mTarget.execute(UseCase.NoParams.NONE);

        // Assert
        verify(mMockRepository, times(1)).query(eq(new UserQuery.GetAllUsersQuery()));
    }

    @Test
    public void thatResultIsReturnedOnMainThread() throws Exception {
        // Setup
        final BaseUser expectedResult = new BaseUser("id", "displayName");
        when(mMockRepository.query(eq(new UserQuery.GetAllUsersQuery())))
            .thenReturn(Observable.just(expectedResult));

        // Execute & Assert
        mTarget.execute(UseCase.NoParams.NONE)
            .doOnNext(__ -> assertTrue("Result not returned on main thread", getSchedulerFactory().isOnMainScheduler(Thread.currentThread())))
            .subscribe();
    }

}
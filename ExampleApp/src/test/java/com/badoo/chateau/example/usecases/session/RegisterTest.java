package com.badoo.chateau.example.usecases.session;

import com.badoo.barf.data.repo.Repository;
import com.badoo.chateau.data.models.BaseUser;
import com.badoo.chateau.example.data.model.ExampleUser;
import com.badoo.chateau.example.data.repos.session.SessionQuery;
import com.badoo.unittest.rx.BaseRxTestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RegisterTest extends BaseRxTestCase {

    @Mock
    private Repository<ExampleUser> mMockRepository;
    private Register mTarget;

    @Before
    public void beforeTest() {
        super.beforeTest();
        mTarget = new Register<>(mMockRepository);
    }

    @Test
    public void whenMessagesForChatRequested_thenRepoIsQueriedForCorrectChatId() throws Exception {
        // Setup
        final String userName = "userName";
        final String displayName = "displayName";
        final String password = "password";
        final BaseUser expectedResult = new BaseUser("id", "displayName");
        when(mMockRepository.query(eq(new SessionQuery.Register<>(userName, displayName, password))))
            .thenReturn(Observable.just(expectedResult));

        // Execute
        mTarget.execute(userName, displayName, password);

        // Assert
        verify(mMockRepository, times(1)).query(eq(new SessionQuery.Register<>(userName, displayName, password)));
    }
}
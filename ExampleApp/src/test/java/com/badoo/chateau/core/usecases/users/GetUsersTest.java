package com.badoo.chateau.core.usecases.users;

import com.badoo.barf.data.repo.Repository;
import com.badoo.chateau.core.repos.users.UserQueries;
import com.badoo.chateau.data.models.BaseUser;
import com.badoo.chateau.example.data.model.ExampleUser;
import com.badoo.unittest.rx.BaseRxTestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import rx.Observable;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GetUsersTest extends BaseRxTestCase {

    @Mock
    private Repository<ExampleUser> mMockRepository;
    private GetUsers mTarget;

    @Before
    public void beforeTest() {
        super.beforeTest();
        mTarget = new GetUsers<>(mMockRepository);
    }

    @Test
    public void whenMessagesForChatRequested_thenRepoIsQueriedForCorrectChatId() throws Exception {
        // Setup
        final BaseUser expectedResult = new BaseUser("id", "displayName");
        when(mMockRepository.query(eq(new UserQueries.GetAllUsersQuery<>())))
            .thenReturn(Observable.just(Collections.singletonList(expectedResult)));

        // Execute
        mTarget.execute();

        // Assert
        verify(mMockRepository, times(1)).query(eq(new UserQueries.GetAllUsersQuery<>()));
    }
}
package com.badoo.chateau.core.usecases.istyping;

import com.badoo.barf.data.repo.Repository;
import com.badoo.chateau.core.model.User;
import com.badoo.chateau.core.repos.istyping.IsTypingQueries;
import com.badoo.chateau.example.data.model.ExampleUser;
import com.badoo.unittest.rx.BaseRxTestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SubscribeToUsersTypingQueryTest extends BaseRxTestCase {

    private static final String CHAT_ID = "chat";

    @Mock
    private Repository<ExampleUser> mMockRepository;
    private SubscribeToUsersTyping mTarget;

    @Before
    public void beforeTest() {
        super.beforeTest();
        mTarget = new SubscribeToUsersTyping<>(mMockRepository);
    }

    @Test
    public void whenMessagesForChatRequested_thenRepoIsQueriedForCorrectChatId() throws Exception {
        // Setup
        User user = mock(User.class);
        when(mMockRepository.query(eq(new IsTypingQueries.SubscribeToUsersTypingQuery<>(CHAT_ID))))
            .thenReturn(Observable.just(user));

        // Execute
        mTarget.execute(CHAT_ID);

        // Assert
        verify(mMockRepository, times(1)).query(eq(new IsTypingQueries.SubscribeToUsersTypingQuery<>(CHAT_ID)));
    }
}
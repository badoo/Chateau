package com.badoo.chateau.core.usecases.conversations;

import com.badoo.barf.data.repo.Repository;
import com.badoo.chateau.core.model.Conversation;
import com.badoo.chateau.core.repos.conversations.ConversationDataSource;
import com.badoo.chateau.core.repos.conversations.ConversationQueries;
import com.badoo.chateau.core.repos.conversations.ConversationQueries.LoadConversationsQuery;
import com.badoo.chateau.core.repos.conversations.ConversationQueries.LoadConversationsQuery.Type;
import com.badoo.chateau.example.data.model.ExampleConversation;
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
public class LoadConversationsTest extends BaseRxTestCase {

    @Mock
    private Repository<TestConversation> mMockRepository;
    private LoadConversations<TestConversation> mTarget;

    @Before
    public void beforeTest() {
        super.beforeTest();
        mTarget = new LoadConversations<>(mMockRepository);
    }

    @Test
    public void whenMessagesForChatRequested_thenRepoIsQueriedForCorrectChatId() throws Exception {
        // Setup
        when(mMockRepository.query(eq(new LoadConversationsQuery<>(Type.ALL))))
            .thenReturn(Observable.just(new ConversationDataSource.LoadResult<>(Collections.emptyList(), false, false)));

        // Execute
        mTarget.execute(Type.ALL);

        // Assert
        verify(mMockRepository, times(1)).query(eq(new LoadConversationsQuery<>(Type.ALL)));
    }

    private static class TestConversation implements Conversation {
    }
}
package com.badoo.chateau.core.usecases.conversations;

import com.badoo.barf.data.repo.Repository;
import com.badoo.chateau.core.repos.conversations.ConversationQueries;
import com.badoo.chateau.example.data.model.ExampleConversation;
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
public class LoadMyConversationsTest extends BaseRxTestCase {

    @Mock
    private Repository<ExampleConversation> mMockRepository;
    private LoadMyConversations mTarget;

    @Before
    public void beforeTest() {
        super.beforeTest();
        mTarget = new LoadMyConversations(mMockRepository);
    }

    @Test
    public void whenMessagesForChatRequested_thenRepoIsQueriedForCorrectChatId() throws Exception {
        // Setup
        when(mMockRepository.query(eq(ConversationQueries.LoadConversationsQuery.query())))
            .thenReturn(Observable.just(true));

        // Execute
        mTarget.execute();

        // Assert
        verify(mMockRepository, times(1)).query(eq(ConversationQueries.LoadConversationsQuery.query()));
    }
}
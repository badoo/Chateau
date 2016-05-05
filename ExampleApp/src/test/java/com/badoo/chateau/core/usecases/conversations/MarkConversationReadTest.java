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
public class MarkConversationReadTest extends BaseRxTestCase {

    @Mock
    private Repository<ExampleConversation> mMockRepository;
    private MarkConversationRead mTarget;

    @Before
    public void beforeTest() {
        super.beforeTest();
        mTarget = new MarkConversationRead(mMockRepository);
    }

    @Test
    public void whenMessagesForChatRequested_thenRepoIsQueriedForCorrectChatId() throws Exception {
        // Setup
        final String chatId = "chatId";
        when(mMockRepository.query(eq(new ConversationQueries.MarkConversationReadQuery(chatId))))
            .thenReturn(Observable.empty());

        // Execute
        mTarget.execute(chatId);

        // Assert
        verify(mMockRepository, times(1)).query(eq(new ConversationQueries.MarkConversationReadQuery(chatId)));
    }
}
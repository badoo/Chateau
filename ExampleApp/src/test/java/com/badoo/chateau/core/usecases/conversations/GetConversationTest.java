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

import java.util.Collections;

import rx.Observable;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GetConversationTest extends BaseRxTestCase {

    @Mock
    private Repository<ExampleConversation> mMockRepository;
    private GetConversation<ExampleConversation> mTarget;

    @Before
    public void beforeTest() {
        super.beforeTest();
        mTarget = new GetConversation<>(mMockRepository);
    }

    @Test
    public void whenMessagesForChatRequested_thenRepoIsQueriedForCorrectChatId() throws Exception {
        // Setup
        final String chatId = "chatId";
        final ExampleConversation conversation = new ExampleConversation("id", "name", Collections.emptyList(), null, 0);
        when(mMockRepository.query(eq(new ConversationQueries.GetConversationQuery<>(chatId))))
            .thenReturn(Observable.just(conversation));

        // Execute
        mTarget.execute(chatId);

        // Assert
        verify(mMockRepository, times(1)).query(eq(new ConversationQueries.GetConversationQuery<>(chatId)));
    }
}
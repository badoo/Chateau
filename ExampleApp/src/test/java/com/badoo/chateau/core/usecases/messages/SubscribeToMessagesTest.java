package com.badoo.chateau.core.usecases.messages;

import com.badoo.barf.data.repo.Repository;
import com.badoo.chateau.core.model.Message;
import com.badoo.chateau.core.repos.messages.MessageQueries;
import com.badoo.chateau.example.data.model.ExampleMessage;
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
public class SubscribeToMessagesTest extends BaseRxTestCase {

    @Mock
    private Repository<ExampleMessage> mMockRepository;
    private SubscribeToMessages mTarget;

    @Before
    public void beforeTest() {
        super.beforeTest();
        mTarget = new SubscribeToMessages<>(mMockRepository);
    }

    @Test
    public void whenMessagesForChatRequested_thenRepoIsQueriedForCorrectChatId() throws Exception {
        // Setup
        final String chatId = "chatId";
        final Message expectedResult = null;
        when(mMockRepository.query(eq(new MessageQueries.SubscribeToMessagesQuery<>(chatId))))
            .thenReturn(Observable.just(Collections.singletonList(expectedResult)));

        // Execute
        mTarget.execute(chatId);

        // Assert
        verify(mMockRepository, times(1)).query(eq(new MessageQueries.SubscribeToMessagesQuery<>(chatId)));
    }
}
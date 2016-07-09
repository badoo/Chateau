package com.badoo.chateau.core.usecases.messages;

import com.badoo.barf.data.repo.Repository;
import com.badoo.chateau.core.repos.messages.MessageQueries;
import com.badoo.chateau.example.data.model.ExampleMessage;
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
public class SendMessagesTest extends BaseRxTestCase {

    @Mock
    private Repository<ExampleMessage> mMockRepository;
    private SendMessage<ExampleMessage> mTarget;

    @Before
    public void beforeTest() {
        super.beforeTest();
        mTarget = new SendMessage<>(mMockRepository);
    }

    @Test
    public void whenMessagesForChatSent_thenRepoIsQueriedWithSentMessage() throws Exception {
        // Setup
        final String conversationId = "conversationId";
        ExampleMessage message = ExampleMessage.createOutgoingTextMessage(conversationId, "message");

        when(mMockRepository.query(eq(new MessageQueries.SendQuery<>(conversationId, message))))
            .thenReturn(Observable.never());

        // Execute
        mTarget.execute(conversationId, message);

        // Assert
        verify(mMockRepository, times(1)).query(eq(new MessageQueries.SendQuery<>(conversationId, message)));
    }
}
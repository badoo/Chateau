package com.badoo.chateau.example.data.repos.messages;

import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import com.badoo.chateau.core.repos.messages.MessageDataSource.Update;
import com.badoo.chateau.core.repos.messages.MessageQueries;
import com.badoo.chateau.example.data.model.ExampleMessage;
import com.badoo.chateau.example.data.repos.messages.ParseMessageDataSource.ImageUploader;
import com.badoo.chateau.example.data.util.ParseHelper;
import com.badoo.chateau.example.data.util.ParseUtils;
import com.badoo.chateau.example.data.util.ParseUtils.MessagesTable;
import com.badoo.unittest.rx.BaseRxTestCase;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.hamcrest.CustomMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ParseMessageDataSourceTest extends BaseRxTestCase {

    private static final String TEST_CHAT_ID = "chatId";

    @Mock
    private ImageUploader mImageUploader;
    @Mock
    private ParseHelper mMockParseHelper;
    @Mock
    private LocalBroadcastManager mBroadcastManager;
    @Mock
    private ParseUser mUser;

    private ParseMessageDataSource mTarget;

    // Use to listen for publishes of sent messages
    private Observable<Update<ExampleMessage>> mUpdates;

    @Before
    public void beforeTest() {
        super.beforeTest();
        mTarget = new ParseMessageDataSource(mBroadcastManager, mImageUploader, mMockParseHelper);
        mUpdates = mTarget.subscribe(new MessageQueries.SubscribeQuery<>(TEST_CHAT_ID));
        mockCurrentUser("userId");
    }

    @Test
    public void sendTextMessage() {
        // Setup
        ExampleMessage message = ExampleMessage.createOutgoingTextMessage(TEST_CHAT_ID, "Hello world");
        final MessageQueries.SendQuery<ExampleMessage> sendQuery = new MessageQueries.SendQuery<>(TEST_CHAT_ID, message);
        final TestSubscriber<Update<ExampleMessage>> testSubscriber = new TestSubscriber<>();

        // Execute
        mUpdates.subscribe(testSubscriber);
        mTarget.send(sendQuery).subscribe();

        // Assert
        verify(mMockParseHelper).save(argThat(new CustomMatcher<ParseObject>("") {
            @Override
            public boolean matches(Object item) {
                final ParseObject parseMessage = (ParseObject) item;

                return parseMessage.getClassName().equals(MessagesTable.NAME);
            }
        }));
        testSubscriber.assertValueCount(2);
    }

    private ParseUser mockCurrentUser(@NonNull String userId) {
        final ParseUser mockUser = mock(ParseUser.class);
        when(mockUser.getObjectId()).thenReturn(userId);
        when(mMockParseHelper.getCurrentUser()).thenReturn(mockUser);
        return mockUser;
    }

}

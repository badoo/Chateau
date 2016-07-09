package com.badoo.chateau.example.data.repos.conversations;

import android.support.annotation.NonNull;

import com.badoo.chateau.core.repos.conversations.ConversationQueries;
import com.badoo.chateau.core.repos.conversations.ConversationQueries.LoadConversationsQuery.Type;
import com.badoo.chateau.example.data.model.ExampleConversation;
import com.badoo.chateau.example.data.util.ParseHelper;
import com.badoo.chateau.example.data.util.ParseUtils.ChatSubscriptionTable;
import com.badoo.chateau.example.data.util.ParseUtils.CreateChatFunc;
import com.badoo.chateau.example.data.util.ParseUtils.DeleteConversationsFunc;
import com.badoo.chateau.example.data.util.ParseUtils.GetMySubscriptionsFunc;
import com.badoo.chateau.example.data.util.ParseUtils.MarkChatReadFunc;
import com.badoo.unittest.MapMatchers;
import com.badoo.unittest.ModelTestHelper;
import com.badoo.unittest.rx.BaseRxTestCase;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.observers.TestSubscriber;

import static com.badoo.chateau.core.repos.conversations.ConversationQueries.CreateConversationQuery;
import static com.badoo.chateau.core.repos.conversations.ConversationQueries.CreateGroupConversationQuery;
import static com.badoo.chateau.core.repos.conversations.ConversationQueries.DeleteConversationsQuery;
import static com.badoo.chateau.core.repos.conversations.ConversationQueries.GetConversationQuery;
import static com.badoo.chateau.core.repos.conversations.ConversationQueries.LoadConversationsQuery;
import static com.badoo.chateau.core.repos.conversations.ConversationQueries.MarkConversationReadQuery;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ParseConversationDataSourceTest extends BaseRxTestCase {

    @Mock
    private ParseHelper mMockParseHelper;

    @Captor
    ArgumentCaptor<ParseQuery<ParseObject>> mParseQueryArgumentCaptor;

    private ParseConversationDataSource mTarget;

    @Before
    public void beforeTest() {
        super.beforeTest();
        mTarget = new ParseConversationDataSource(mMockParseHelper);
    }

    @Test
    public void getConversationsForLoggedInUser() {
        // Setup
        final List<ParseObject> subscriptions = ModelTestHelper.createSubscriptions(10);
        when(mMockParseHelper.<List<ParseObject>>callFunction(GetMySubscriptionsFunc.NAME, Collections.emptyMap()))
            .thenReturn(Observable.just(subscriptions));
        final TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        mTarget.subscribe(new ConversationQueries.SubscribeToUpdatesQuery()).subscribe(testSubscriber);

        // Execute
        executeTarget(mTarget.load(new LoadConversationsQuery<>(Type.ALL)));

        // Assert
        assertThat(testSubscriber.getOnNextEvents().size(), is(0));
    }

    @Test
    public void getConversation() {
        // Setup
        final String userId = "userId";
        mockCurrentUser(userId);

        final List<ParseObject> subscriptions = ModelTestHelper.createSubscriptions(10);
        final String chatId = "chatId";
        when(mMockParseHelper.find(Mockito.<ParseQuery<ParseObject>>any())).thenReturn(Observable.just(subscriptions));

        // Execute
        final TestSubscriber<ExampleConversation> testSubscriber = executeTarget(mTarget.get(new GetConversationQuery(chatId)));

        // Assert
        testSubscriber.assertCompleted();
        verify(mMockParseHelper).find(mParseQueryArgumentCaptor.capture());

        final ParseQuery<ParseObject> parseQuery = mParseQueryArgumentCaptor.getValue();
        assertThat(testSubscriber.getOnNextEvents().size(), is(10));
        assertThat(parseQuery.getClassName(), is(ChatSubscriptionTable.NAME));
    }

    @Test
    public void createGroupConversation() {
        // Setup
        final List<String> userIds = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            userIds.add(Integer.toString(i));
        }
        final String groupName = "groupName";

        final Map<String, Object> expectedParams = new HashMap<>();
        expectedParams.put(CreateChatFunc.Fields.OTHER_USER_IDS, userIds);
        expectedParams.put(CreateChatFunc.Fields.GROUP_NAME, groupName);

        ParseObject subscription = ModelTestHelper.createSubscription("id");
        when(mMockParseHelper.<ParseObject>callFunction(eq(CreateChatFunc.NAME), argThat(MapMatchers.matchesEntriesIn(expectedParams))))
            .thenReturn(Observable.just(subscription));

        // Execute
        final TestSubscriber<ExampleConversation> testSubscriber = executeTarget(
            mTarget.create(new CreateGroupConversationQuery(userIds, groupName)));

        // Assert
        testSubscriber.assertCompleted();
        assertThat(testSubscriber.getOnNextEvents().size(), is(1));
    }

    @Test
    public void createConversation() {
        // Setup
        final Map<String, Object> expectedParams = new HashMap<>();
        expectedParams.put(CreateChatFunc.Fields.OTHER_USER_IDS, Collections.singletonList("0"));

        ParseObject subscription = ModelTestHelper.createSubscription("id");
        when(mMockParseHelper.<ParseObject>callFunction(eq(CreateChatFunc.NAME), argThat(MapMatchers.matchesEntriesIn(expectedParams))))
            .thenReturn(Observable.just(subscription));

        // Execute
        final TestSubscriber<ExampleConversation> testSubscriber = executeTarget(
            mTarget.create(new CreateConversationQuery("0")));

        // Assert
        testSubscriber.assertCompleted();
        assertThat(testSubscriber.getOnNextEvents().size(), is(1));
    }

    @Test
    public void markConversationRead() {
        // Setup
        String chatId = "chatId";
        final Map<String, Object> expectedParams = new HashMap<>();
        expectedParams.put(MarkChatReadFunc.Fields.CHAT_ID, chatId);
        when(mMockParseHelper.<ParseObject>callFunction(eq(MarkChatReadFunc.NAME), argThat(MapMatchers.matchesEntriesIn(expectedParams))))
            .thenReturn(Observable.empty());

        // Execute
        final TestSubscriber<Void> testSubscriber = executeTarget(
            mTarget.markRead(new MarkConversationReadQuery(chatId)));

        // Assert
        testSubscriber.assertCompleted();
        assertThat(testSubscriber.getOnNextEvents().size(), is(0));
    }

    @Test
    public void deleteConversations() {
        // Setup
        final List<String> chatIds = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            chatIds.add(Integer.toString(i));
        }

        final Map<String, Object> expectedParams = new HashMap<>();
        expectedParams.put(DeleteConversationsFunc.Fields.CHAT_IDS, chatIds);

        final List<ParseObject> subscription = ModelTestHelper.createSubscriptions(10);
        when(mMockParseHelper.<List<ParseObject>>callFunction(eq(DeleteConversationsFunc.NAME), argThat(MapMatchers.matchesEntriesIn(expectedParams))))
            .thenReturn(Observable.just(subscription));


        when(mMockParseHelper.<List<ParseObject>>callFunction(eq(GetMySubscriptionsFunc.NAME), Matchers.<Map<String, Object>>any())).thenReturn(Observable.empty());

        final List<ExampleConversation> conversations = ModelTestHelper.createConversations(10);

        // Execute
        final TestSubscriber<?> testSubscriber = executeTarget(mTarget.delete(new DeleteConversationsQuery<>(conversations)));

        // Assert
        testSubscriber.assertCompleted();
        verify(mMockParseHelper).<List<ParseObject>>callFunction(eq(DeleteConversationsFunc.NAME), argThat(MapMatchers.matchesEntriesIn(expectedParams)));
    }

    private ParseUser mockCurrentUser(@NonNull String userId) {
        final ParseUser mockUser = mock(ParseUser.class);
        when(mockUser.getObjectId()).thenReturn(userId);
        when(mMockParseHelper.getCurrentUser()).thenReturn(mockUser);
        return mockUser;
    }

}

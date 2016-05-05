package com.badoo.chateau.example.data.repos.session;

import com.badoo.chateau.example.Broadcaster;
import com.badoo.chateau.data.models.BaseUser;
import com.badoo.chateau.example.data.util.ParseHelper;
import com.badoo.unittest.ModelTestHelper;
import com.badoo.unittest.rx.BaseRxTestCase;
import com.parse.ParseUser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ParseSessionDataSourceTest extends BaseRxTestCase {

    private static final String USER_ID = "userId";
    private static final String USER_NAME = "username";
    private static final String PASSWORD = "password";
    private static final String DISPLAY_NAME = "displayName";

    private ParseHelper mParseHelper;
    private Broadcaster mBroadcaster;
    private ParseSessionDataSource mTarget;

    @Before
    public void setup() {
        mParseHelper = mock(ParseHelper.class);
        mBroadcaster = mock(Broadcaster.class);
        mTarget = new ParseSessionDataSource(mBroadcaster, mParseHelper);
    }

    @Test
    public void signIn() {
        // Given
        ParseUser currentUser = ModelTestHelper.createParseUser(USER_ID);
        when(mParseHelper.signIn(USER_NAME, PASSWORD)).thenReturn(Observable.just(currentUser));

        // When
        TestSubscriber<BaseUser> testSubscriber = executeTarget(mTarget.signIn(new SessionQuery.SignIn(USER_NAME, PASSWORD)));

        // Then
        assertThat(testSubscriber.getOnNextEvents().size(), is(1));
        BaseUser user = testSubscriber.getOnNextEvents().get(0);
        assertEquals(USER_ID, user.getUserId());
        testSubscriber.assertCompleted();
        verify(mBroadcaster).userSignedIn();
    }

    @Test
    public void register() {
        // Given
        ParseUser currentUser = ModelTestHelper.createParseUser(USER_ID);
        when(mParseHelper.signUp(eq(USER_NAME), eq(PASSWORD), any())).thenReturn(Observable.just(currentUser));

        // When
        TestSubscriber<BaseUser> testSubscriber = executeTarget(mTarget.register(new SessionQuery.Register(USER_NAME, DISPLAY_NAME, PASSWORD)));

        // Then
        assertThat(testSubscriber.getOnNextEvents().size(), is(1));
        BaseUser user = testSubscriber.getOnNextEvents().get(0);
        assertEquals(USER_ID, user.getUserId());
        testSubscriber.assertCompleted();
        verify(mBroadcaster).userSignedIn();
    }

    @Test
    public void signOut() {
        // Given
        when(mParseHelper.signOut()).thenReturn(Observable.empty());

        // When
        TestSubscriber<Void> testSubscriber = executeTarget(mTarget.signOut());

        // Then
        testSubscriber.assertCompleted();
        verify(mBroadcaster).userSignedOut();
    }

}
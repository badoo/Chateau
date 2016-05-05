package com.badoo.chateau.example.data.repos.user;

import com.badoo.chateau.core.repos.users.UserQueries;
import com.badoo.chateau.data.models.BaseUser;
import com.badoo.chateau.example.data.model.ExampleUser;
import com.badoo.chateau.example.data.util.ParseHelper;
import com.badoo.unittest.ModelTestHelper;
import com.badoo.unittest.rx.BaseRxTestCase;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ParseUserDataSourceTest extends BaseRxTestCase {

    private static final String OTHER_USER = "otherUser";
    private static final String CURRENT_USER = "currentUser";

    private ParseHelper mMockParseHelper;
    private ParseUserDataSource mTarget;

    @Before
    public void setup() {
        mMockParseHelper = Mockito.mock(ParseHelper.class);
        mTarget = new ParseUserDataSource(mMockParseHelper);
    }

    @Test
    public void getAllUsers() {
        // Given
        List<ParseUser> users = ModelTestHelper.createParseUsers(10);
        when(mMockParseHelper.find(Mockito.<ParseQuery<ParseUser>>any())).thenReturn(Observable.just(users));
        ParseUser currentUser = ModelTestHelper.createParseUser(CURRENT_USER);
        when(mMockParseHelper.getCurrentUser()).thenReturn(currentUser);

        // When
        TestSubscriber<List<ExampleUser>> testSubscriber = executeTarget(mTarget.getAllUsers(new UserQueries.GetAllUsersQuery()));

        // Then
        assertThat(testSubscriber.getOnNextEvents().size(), is(1));
        testSubscriber.assertCompleted();
    }

    @Test
    public void getSingleUser() {
        // Given
        List<ParseUser> users = Collections.singletonList(ModelTestHelper.createParseUser(OTHER_USER));
        when(mMockParseHelper.find(Mockito.<ParseQuery<ParseUser>>any())).thenReturn(Observable.just(users));

        // When
        TestSubscriber<ExampleUser> testSubscriber = executeTarget(mTarget.getSingleUser(new UserQueries.GetUserQuery(OTHER_USER)));

        // Then
        assertThat(testSubscriber.getOnNextEvents().size(), is(1));
        BaseUser user = testSubscriber.getOnNextEvents().get(0);
        assertEquals(OTHER_USER, user.getUserId());
        testSubscriber.assertCompleted();
    }

}
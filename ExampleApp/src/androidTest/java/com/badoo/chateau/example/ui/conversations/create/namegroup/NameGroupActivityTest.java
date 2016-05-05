package com.badoo.chateau.example.ui.conversations.create.namegroup;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.badoo.chateau.example.BaseTestCase;
import com.badoo.chateau.example.R;
import com.badoo.chateau.example.data.model.ExampleConversation;
import com.badoo.chateau.example.ui.Injector;
import com.badoo.chateau.ui.conversations.create.namegroup.NameGroupPresenter;
import com.badoo.chateau.ui.conversations.create.namegroup.NameGroupPresenter.NameGroupFlowListener;
import com.badoo.chateau.ui.conversations.create.namegroup.NameGroupPresenter.NameGroupView;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class NameGroupActivityTest extends BaseTestCase<NameGroupActivity> {

    private static final String NAME = "group name";
    private NameGroupPresenter mPresenter;

    @Override
    protected Class<NameGroupActivity> getActivityClass() {
        return NameGroupActivity.class;
    }

    @Override
    protected Intent getActivityIntent() {
        List<String> users = Collections.singletonList("");
        return NameGroupActivity.create(InstrumentationRegistry.getContext(), users);
    }

    @Override
    protected void beforeActivityLaunched() {
        mPresenter = mock(NameGroupPresenter.class);
        Injector.register(NameGroupActivity.class, new NameGroupActivity.DefaultConfiguration() {
            @Override
            protected NameGroupPresenter createNameGroupPresenter(@NonNull NameGroupView nameGroupView, @NonNull NameGroupFlowListener<ExampleConversation> flowListener, List<String> userIds) {
                return mPresenter;
            }
        });
    }

    @Test
    public void setNameForGroup() {
        // Given
        onView(withId(R.id.nameGroup_groupName_inner)).perform(typeText(NAME));

        // When
        onView(withId(R.id.nameGroup_createGroup)).perform(click());

        // Then
        verify(mPresenter).onCreateGroupClicked(NAME);
    }
}
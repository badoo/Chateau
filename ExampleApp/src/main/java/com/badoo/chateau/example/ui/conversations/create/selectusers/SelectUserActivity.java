package com.badoo.chateau.example.ui.conversations.create.selectusers;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.badoo.barf.mvp.PresenterFactory;
import com.badoo.chateau.core.usecases.conversations.CreateConversation;
import com.badoo.chateau.core.usecases.users.GetUsers;
import com.badoo.chateau.example.R;
import com.badoo.chateau.example.data.model.ExampleConversation;
import com.badoo.chateau.example.data.model.ExampleUser;
import com.badoo.chateau.example.ui.BaseActivity;
import com.badoo.chateau.example.ui.ExampleConfiguration;
import com.badoo.chateau.example.ui.Injector;
import com.badoo.chateau.example.ui.chat.ChatActivity;
import com.badoo.chateau.example.ui.conversations.create.namegroup.NameGroupActivity;
import com.badoo.chateau.extras.ViewFinder;
import com.badoo.chateau.ui.conversations.create.selectusers.UserListPresenter;
import com.badoo.chateau.ui.conversations.create.selectusers.UserListPresenter.UserListFlowListener;
import com.badoo.chateau.ui.conversations.create.selectusers.UserListPresenter.UserListView;
import com.badoo.chateau.ui.conversations.create.selectusers.UserListPresenterImpl;

import java.util.ArrayList;
import java.util.List;

public class SelectUserActivity extends BaseActivity implements UserListFlowListener<ExampleConversation, ExampleUser> {

    public static class DefaultConfiguration extends ExampleConfiguration<SelectUserActivity> {

        @Override
        public void inject(SelectUserActivity target) {
            createView(target);
        }

        protected UserListView<ExampleUser> createView(@NonNull SelectUserActivity activity) {
            final PresenterFactory<UserListView<ExampleUser>, UserListPresenter<ExampleUser>> presenterFactory = new PresenterFactory<>(v -> createUserListPresenter(v, activity));
            final UserListViewImpl view = new UserListViewImpl(ViewFinder.from(activity), presenterFactory);
            activity.registerPresenter(presenterFactory.get());
            activity.registerBackPressedListener(view);
            return view;
        }

        protected UserListPresenter createUserListPresenter(@NonNull UserListView<ExampleUser> userListView, @NonNull UserListFlowListener<ExampleConversation, ExampleUser> flowListener) {
            return new UserListPresenterImpl<>(userListView, flowListener, new GetUsers<>(getUserRepo()), new CreateConversation<>(getConversationRepo()));
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_conversation);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Injector.inject(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void requestOpenChat(@NonNull ExampleConversation conversation) {
        final Intent intent = ChatActivity.create(this, conversation.getId(), "");
        finish();
        startActivity(intent);
    }

    @Override
    public void requestCreateGroupChat(@NonNull List<ExampleUser> users) {
        final List<String> userIds = new ArrayList<>(users.size());
        for (ExampleUser user : users) {
            userIds.add(user.getUserId());
        }
        final Intent intent = NameGroupActivity.create(this, userIds);
        startActivity(intent);
    }
}

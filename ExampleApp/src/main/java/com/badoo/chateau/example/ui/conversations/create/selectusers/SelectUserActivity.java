package com.badoo.chateau.example.ui.conversations.create.selectusers;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.badoo.chateau.example.R;

import com.badoo.chateau.data.models.BaseUser;
import com.badoo.chateau.example.ui.BaseActivity;
import com.badoo.chateau.example.ui.Injector;
import com.badoo.chateau.example.ui.chat.ChatActivity;
import com.badoo.chateau.example.ui.conversations.create.namegroup.NameGroupActivity;
import com.badoo.chateau.example.ui.util.ViewFinder;
import com.badoo.chateau.ui.conversations.create.selectusers.UserListPresenter;
import com.badoo.chateau.ui.conversations.create.selectusers.UserListPresenterImpl;
import com.badoo.chateau.ui.conversations.create.selectusers.UserListView;

import java.util.List;

public class SelectUserActivity extends BaseActivity implements UserListPresenter.UserListFlowListener {

    public static class DefaultConfiguration extends Injector.BaseConfiguration<SelectUserActivity> {

        @Override
        public void inject(SelectUserActivity target) {
            final UserListView view = createView(target);
            final UserListPresenter presenter = createPresenter();
            bind(view, presenter, target);
            target.setUserListPresenter(presenter);
        }

        protected UserListView createView(SelectUserActivity activity) {
            final UserListViewImpl view = new UserListViewImpl(ViewFinder.from(activity));
            activity.registerBackPressedListener(view);
            return view;
        }

        protected UserListPresenter createPresenter() {
            return new UserListPresenterImpl();
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

    public void setUserListPresenter(UserListPresenter userListPresenter) {
        registerPresenter(userListPresenter);
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
    public void openChat(@NonNull String chatId) {
        final Intent intent = ChatActivity.create(this, chatId, "");
        finish();
        startActivity(intent);
    }

    @Override
    public void createGroupChat(@NonNull List<BaseUser> users) {
        final Intent intent = NameGroupActivity.create(this, users);
        startActivity(intent);
    }
}

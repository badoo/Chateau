package com.badoo.chateau.example.ui.conversations.create.namegroup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.badoo.chateau.example.R;
import com.badoo.chateau.data.models.BaseUser;
import com.badoo.chateau.example.ui.BaseActivity;
import com.badoo.chateau.example.ui.Injector;
import com.badoo.chateau.example.ui.chat.ChatActivity;
import com.badoo.chateau.example.ui.conversations.list.ConversationListActivity;
import com.badoo.chateau.core.model.User;
import com.badoo.chateau.extras.ViewFinder;
import com.badoo.chateau.ui.conversations.create.namegroup.NameGroupPresenter;
import com.badoo.chateau.ui.conversations.create.namegroup.NameGroupPresenterImpl;
import com.badoo.chateau.ui.conversations.create.namegroup.NameGroupView;

import java.util.ArrayList;
import java.util.List;


public class NameGroupActivity extends BaseActivity implements NameGroupPresenter.NameGroupFlowListener {

    public static class DefaultConfiguration extends Injector.BaseConfiguration<NameGroupActivity> {

        @Override
        public void inject(@NonNull NameGroupActivity target) {
            final List<User> users = new ArrayList<>(target.getIntent().getParcelableArrayListExtra(EXTRA_USERS));
            final NameGroupView view = new NameGroupViewImpl(ViewFinder.from(target));
            final NameGroupPresenter presenter = createPresenter(users);
            bind(view, presenter, target);
            target.setNameGroupPresenter(presenter);
        }

        protected NameGroupPresenter createPresenter(final List<User> users) {
            return new NameGroupPresenterImpl(users);
        }
    }

    private static final String EXTRA_USERS = NameGroupActivity.class.getName() + ":users";

    public static Intent create(Context context, List<BaseUser> users) {
        final Intent intent = new Intent(context, NameGroupActivity.class);
        intent.putParcelableArrayListExtra(EXTRA_USERS, new ArrayList<>(users));
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_name_group);
        setTitle(R.string.title_activity_select_users);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Injector.inject(this);
    }


    public void setNameGroupPresenter(NameGroupPresenter nameGroupPresenter) {
        registerPresenter(nameGroupPresenter);
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
        final Intent startConversations = new Intent(this, ConversationListActivity.class);
        startConversations.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final Intent startChat = ChatActivity.create(this, chatId, "");
        startActivities(new Intent[]{startConversations, startChat});
    }
}

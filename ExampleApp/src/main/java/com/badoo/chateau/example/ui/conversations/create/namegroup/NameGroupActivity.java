package com.badoo.chateau.example.ui.conversations.create.namegroup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.badoo.barf.mvp.PresenterFactory;
import com.badoo.chateau.core.usecases.conversations.CreateGroupConversation;
import com.badoo.chateau.example.R;
import com.badoo.chateau.example.data.model.ExampleConversation;
import com.badoo.chateau.example.ui.BaseActivity;
import com.badoo.chateau.example.ui.ExampleConfiguration;
import com.badoo.chateau.example.ui.Injector;
import com.badoo.chateau.example.ui.chat.ChatActivity;
import com.badoo.chateau.example.ui.conversations.list.ConversationListActivity;
import com.badoo.chateau.extras.ViewFinder;
import com.badoo.chateau.ui.conversations.create.namegroup.NameGroupPresenter;
import com.badoo.chateau.ui.conversations.create.namegroup.NameGroupPresenterImpl;

import java.util.ArrayList;
import java.util.List;

import static com.badoo.chateau.ui.conversations.create.namegroup.NameGroupPresenter.*;

/**
 * Activity for selecting the name of a new group chat
 */
public class NameGroupActivity extends BaseActivity
    implements NameGroupFlowListener<ExampleConversation> {

    /**
     * Entry point for NameGroupActivity
     *
     * @param userIds the list of users to include in this group
     */
    public static Intent create(Context context, List<String> userIds) {
        final Intent intent = new Intent(context, NameGroupActivity.class);
        intent.putStringArrayListExtra(EXTRA_USER_IDS, new ArrayList<>(userIds));
        return intent;
    }

    public static class DefaultConfiguration extends ExampleConfiguration<NameGroupActivity> {

        @Override
        public void inject(@NonNull NameGroupActivity target) {
            final List<String> userIds = new ArrayList<>(target.getIntent().getStringArrayListExtra(EXTRA_USER_IDS));
            final PresenterFactory<NameGroupView, NameGroupPresenter> presenterFactory = new PresenterFactory<>(v -> createNameGroupPresenter(v, target, userIds));
            new NameGroupViewImpl(ViewFinder.from(target), presenterFactory);
            target.registerPresenter(presenterFactory.get());
        }

        protected NameGroupPresenter createNameGroupPresenter(@NonNull NameGroupView nameGroupView, @NonNull NameGroupFlowListener<ExampleConversation> flowListener, List<String> userIds) {
            return new NameGroupPresenterImpl<>(nameGroupView, flowListener, userIds, new CreateGroupConversation<>(getConversationRepo()));
        }
    }

    private static final String EXTRA_USER_IDS = NameGroupActivity.class.getName() + ":users";

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
        final Intent startConversations = new Intent(this, ConversationListActivity.class);
        startConversations.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final Intent startChat = ChatActivity.create(this, conversation.getId(), "");
        startActivities(new Intent[]{startConversations, startChat});
    }
}

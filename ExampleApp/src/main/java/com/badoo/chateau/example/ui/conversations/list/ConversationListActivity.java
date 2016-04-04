package com.badoo.chateau.example.ui.conversations.list;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.badoo.chateau.example.R;
import com.badoo.chateau.data.models.BaseConversation;
import com.badoo.chateau.example.ui.BaseActivity;
import com.badoo.chateau.example.ui.Injector;
import com.badoo.chateau.example.ui.chat.ChatActivity;
import com.badoo.chateau.example.ui.conversations.create.selectusers.SelectUserActivity;
import com.badoo.chateau.extras.ViewFinder;
import com.badoo.chateau.ui.conversations.list.ConversationListPresenter;
import com.badoo.chateau.ui.conversations.list.ConversationListPresenterImpl;
import com.badoo.chateau.ui.conversations.list.ConversationListView;

public class ConversationListActivity extends BaseActivity implements ConversationListPresenter.ConversationListFlowListener {

    public static class DefaultConfiguration extends Injector.BaseConfiguration<ConversationListActivity> {

        @Override
        public void inject(ConversationListActivity target) {
            final ConversationListView view = createView(target);
            final ConversationListPresenter presenter = createPresenter();
            bind(view, presenter, target);
            target.setConversationListPresenter(presenter);
        }

        protected ConversationListView createView(ConversationListActivity activity) {
            return new ConversationListViewImpl(ViewFinder.from(activity), activity.getToolbar());
        }

        protected ConversationListPresenter createPresenter() {
            return new ConversationListPresenterImpl();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_conversations);
        setTitle(R.string.title_activity_conversations);
        Injector.inject(this);
    }

    public void setConversationListPresenter(@NonNull ConversationListPresenter presenter) {
        registerPresenter(presenter);
    }

    @Override
    public void openConversation(@NonNull BaseConversation conversation) {
        final Intent intent = ChatActivity.create(this, conversation.getId(), conversation.getName());
        startActivity(intent);
    }

    @Override
    public void createNewConversation() {
        final Intent intent = new Intent(this, SelectUserActivity.class);
        startActivity(intent);
    }
}

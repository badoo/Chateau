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
import com.badoo.chateau.example.ui.conversations.list.CreateConversationPresenter.CreateConversationFlowListener;
import com.badoo.chateau.example.ui.conversations.list.CreateConversationPresenter.CreateConversationView;
import com.badoo.chateau.extras.ViewFinder;
import com.badoo.chateau.ui.conversations.list.ConversationListPresenter;
import com.badoo.chateau.ui.conversations.list.ConversationListPresenter.ConversationListFlowListener;
import com.badoo.chateau.ui.conversations.list.ConversationListPresenter.ConversationListView;
import com.badoo.chateau.ui.conversations.list.ConversationListPresenterImpl;

public class ConversationListActivity extends BaseActivity implements ConversationListFlowListener, CreateConversationFlowListener {

    public static class DefaultConfiguration extends Injector.BaseConfiguration<ConversationListActivity> {

        @Override
        public void inject(ConversationListActivity target) {
            // Conversation list
            final ConversationListView view = createConversationListView(target);
            final ConversationListPresenter presenter = createConversationListPresenter();
            bind(view, presenter, target);
            target.setConversationListPresenter(presenter);
            // Creating new conversation
            final CreateConversationView createConversationView = createCreateConversationView(target);
            final CreateConversationPresenter createConversationPresenter = createCreateConversationPresenter();
            bind(createConversationView, createConversationPresenter, target);
        }

        protected ConversationListView createConversationListView(ConversationListActivity activity) {
            return new ConversationListViewImpl(ViewFinder.from(activity), activity.getToolbar());
        }

        protected ConversationListPresenter createConversationListPresenter() {
            return new ConversationListPresenterImpl();
        }

        protected CreateConversationView createCreateConversationView(ConversationListActivity activity) {
            return new CreateConversationViewImpl(ViewFinder.from(activity));
        }

        protected CreateConversationPresenter createCreateConversationPresenter() {
            return new CreateConversationPresenterImpl();
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
    public void requestOpenConversation(@NonNull BaseConversation conversation) {
        final Intent intent = ChatActivity.create(this, conversation.getId(), conversation.getName());
        startActivity(intent);
    }

    @Override
    public void requestCreateNewConversation() {
        final Intent intent = new Intent(this, SelectUserActivity.class);
        startActivity(intent);
    }
}

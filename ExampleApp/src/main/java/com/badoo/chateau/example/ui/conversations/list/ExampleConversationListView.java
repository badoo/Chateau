package com.badoo.chateau.example.ui.conversations.list;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.badoo.barf.mvp.MvpView;
import com.badoo.barf.mvp.PresenterFactory;
import com.badoo.chateau.example.R;
import com.badoo.chateau.example.data.model.ExampleConversation;
import com.badoo.chateau.example.ui.util.recycle.AboveFabItemDecoration;
import com.badoo.chateau.extras.MultiSelectionHelper;
import com.badoo.chateau.extras.ViewFinder;
import com.badoo.chateau.ui.conversations.list.ConversationByLastMessageComparator;
import com.badoo.chateau.ui.conversations.list.ConversationListPresenter;
import com.badoo.chateau.ui.conversations.list.ConversationListPresenter.ConversationListView;

import java.util.Collections;
import java.util.List;

class ExampleConversationListView implements ConversationListView<ExampleConversation>,
    MultiSelectionHelper.OnModeChangedListener,
    MultiSelectionHelper.OnSelectionChangedListener,
    ConversationViewHolder.OnConversationClickedListener, MvpView {

    private final ContentLoadingProgressBar mProgress;
    private final MultiSelectionHelper mSelectionHelper;
    @NonNull
    private final Toolbar mToolbar;
    private final FloatingActionButton mStartNewChat;
    private final ConversationsAdapter mAdapter;
    private final RecyclerView mConversationList;
    private final ConversationListPresenter<ExampleConversation> mPresenter;
    private android.view.ActionMode mCurrentActionMode;

    public ExampleConversationListView(@NonNull ViewFinder viewFinder, @NonNull Toolbar toolbar,
                                       @NonNull PresenterFactory<ConversationListView<ExampleConversation>, ConversationListPresenter<ExampleConversation>> presenterFactory) {

        mPresenter = presenterFactory.init(this);
        mToolbar = toolbar;
        mAdapter = new ConversationsAdapter(this);
        mProgress = viewFinder.findViewById(R.id.conversations_progress);
        mSelectionHelper = new MultiSelectionHelper(mAdapter, this, this);
        mAdapter.setSelectionHelper(mSelectionHelper);
        mConversationList = viewFinder.findViewById(R.id.conversations_list);
        mConversationList.setLayoutManager(new LinearLayoutManager(mConversationList.getContext()));
        mConversationList.setAdapter(mAdapter);
        mStartNewChat = viewFinder.findViewById(R.id.conversations_start_new_chat_button);
        mConversationList.addItemDecoration(new AboveFabItemDecoration(mStartNewChat));
    }

    @Override
    public void showConversations(List<ExampleConversation> conversations) {
        Collections.sort(conversations, new ConversationByLastMessageComparator());
        mAdapter.setConversations(conversations);
    }

    @Override
    public void showError(boolean fatal, @Nullable Throwable throwable) {
        if (fatal) {
            Snackbar.make(mConversationList, R.string.error_generic, Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    @Override
    public void showLoading() {
        if (mAdapter.getItemCount() == 0) {
            mProgress.setVisibility(View.VISIBLE);
            mProgress.show();
        }
    }

    @Override
    public void hideLoading() {
        mProgress.hide();
    }

    @Override
    public void onModeChanged(@MultiSelectionHelper.Mode int multiSelect) {
        if (multiSelect == MultiSelectionHelper.MODE_MULTIPLE_SELECT) {
            mStartNewChat.hide();
            mCurrentActionMode = mToolbar.startActionMode(new DeleteConversationActionCallback());
            onSelectionChanged(1); // Trigger the update manually since the first item has already been selected and we won't get a callback for it
        }
        else {
            mStartNewChat.show();
            if (mCurrentActionMode != null) {
                mCurrentActionMode.finish();
            }
        }
    }

    @Override
    public void onSelectionChanged(int count) {
        if (mCurrentActionMode != null) {
            mCurrentActionMode.setTitle(getResources().getString(R.string.title_selected_items, count));
        }
    }

    @NonNull
    private Resources getResources() {
        return mToolbar.getResources();
    }

    @Override
    public void onConversationClicked(@NonNull ExampleConversation conversation) {
        mPresenter.onConversationClicked(conversation);
    }

    private class DeleteConversationActionCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.conversation_actions, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.action_delete) {
                mPresenter.onDeleteConversations(mAdapter.getSelectedConversations());
                mode.finish();
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mCurrentActionMode = null;
            mSelectionHelper.clearSelectedPositions();
        }
    }
}

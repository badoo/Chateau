package com.badoo.chateau.example.ui.conversations.list;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
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

import com.badoo.barf.mvp.BaseView;
import com.badoo.chateau.example.R;
import com.badoo.chateau.data.models.BaseConversation;
import com.badoo.chateau.example.ui.util.recycle.AboveFabItemDecoration;
import com.badoo.chateau.extras.MultiSelectionHelper;
import com.badoo.chateau.extras.ViewFinder;
import com.badoo.chateau.ui.conversations.list.ConversationListPresenter;
import com.badoo.chateau.ui.conversations.list.ConversationListView;

import java.util.List;

class ConversationListViewImpl extends BaseView<ConversationListPresenter> implements ConversationListView, View.OnClickListener, MultiSelectionHelper.OnModeChangedListener, MultiSelectionHelper.OnSelectionChangedListener, ConversationViewHolder.OnConversationClickedListener {

    private final ContentLoadingProgressBar mProgress;
    private final MultiSelectionHelper mSelectionHelper;
    @NonNull
    private final Toolbar mToolbar;
    private final FloatingActionButton mStartNewChat;
    private final ConversationsAdapter mAdapter;
    private final RecyclerView mConversationList;
    private android.view.ActionMode mCurrentActionMode;

    public ConversationListViewImpl(@NonNull ViewFinder viewFinder, @NonNull Toolbar toolbar) {
        mToolbar = toolbar;
        mAdapter = new ConversationsAdapter(this);
        mProgress = viewFinder.findViewById(R.id.conversations_progress);
        mSelectionHelper = new MultiSelectionHelper(mAdapter, this, this);
        mAdapter.setSelectionHelper(mSelectionHelper);
        mConversationList = viewFinder.findViewById(R.id.conversations_list);
        mConversationList.setLayoutManager(new LinearLayoutManager(mConversationList.getContext()));
        mConversationList.setAdapter(mAdapter);

        mStartNewChat = viewFinder.findViewById(R.id.conversations_start_new_chat_button);
        mStartNewChat.setOnClickListener(this);
        mConversationList.addItemDecoration(new AboveFabItemDecoration(mStartNewChat));
    }

    @Override
    public void showConversation(@NonNull BaseConversation conversation) {
        if (mAdapter.updateConversation(conversation)) {
            mConversationList.scrollToPosition(0);
        }
    }

    @Override
    public void showConversations(List<BaseConversation> conversations) {
        mAdapter.setConversations(conversations);
    }

    @Override
    public void removeConversations(@NonNull List<BaseConversation> conversations) {
        mAdapter.removeConversations(conversations);
    }

    public void showError(@StringRes int messageResourceId) {
        Snackbar.make(mConversationList, messageResourceId, Snackbar.LENGTH_INDEFINITE).show();
    }

    @Override
    public void showLoading() {
        if(mAdapter.getItemCount() == 0) {
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
    public void onClick(View v) {
        if (v.getId() == R.id.conversations_start_new_chat_button) {
            getPresenter().onCreateNewConversationClicked();
        }
    }

    @Override
    public void onConversationClicked(@NonNull BaseConversation conversation) {
        getPresenter().onConversationClicked(conversation);
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
                getPresenter().onDeleteConversations(mAdapter.getSelectedConversations());
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

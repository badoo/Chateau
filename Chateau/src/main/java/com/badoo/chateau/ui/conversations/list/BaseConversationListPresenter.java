package com.badoo.chateau.ui.conversations.list;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.badoo.barf.mvp.BaseRxPresenter;
import com.badoo.barf.rx.ScheduleOn;
import com.badoo.chateau.core.model.Conversation;
import com.badoo.chateau.core.repos.conversations.ConversationDataSource.LoadResult;
import com.badoo.chateau.core.usecases.conversations.DeleteConversations;
import com.badoo.chateau.core.usecases.conversations.LoadConversations;
import com.badoo.chateau.core.usecases.conversations.SubscribeToConversationUpdates;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class BaseConversationListPresenter<C extends Conversation> extends BaseRxPresenter implements ConversationListPresenter<C> {

    private static final String TAG = BaseConversationListPresenter.class.getSimpleName();
    private static final boolean DEBUG = true;

    @NonNull
    private final ConversationListView<C> mView;
    @NonNull
    private final ConversationListFlowListener<C> mFlowListener;
    @NonNull
    private final LoadConversations<C> mLoadConversations;
    @NonNull
    private final SubscribeToConversationUpdates mSubscribeToConversationUpdates;
    @NonNull
    private final DeleteConversations mDeleteConversations;
    @Nullable
    private LoadResult<C> mLastLoadResult;

    public BaseConversationListPresenter(@NonNull ConversationListView<C> view,
                                         @NonNull ConversationListFlowListener<C> flowListener,
                                         @NonNull LoadConversations<C> loadConversations,
                                         @NonNull SubscribeToConversationUpdates subscribeToConversationUpdates,
                                         @NonNull DeleteConversations deleteConversations) {
        mView = view;
        mFlowListener = flowListener;
        mLoadConversations = loadConversations;
        mSubscribeToConversationUpdates = subscribeToConversationUpdates;
        mDeleteConversations = deleteConversations;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Setup subscriptions for updates to the list
        manage(mSubscribeToConversationUpdates.execute()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(ignored -> loadNewerConversations(), this::onFatalError));
        // Request data
        reloadConversations();
    }

    @Override
    public void onConversationClicked(@NonNull C conversation) {
        mFlowListener.requestOpenConversation(conversation);
    }

    @Override
    public void onDeleteConversations(@NonNull List<C> conversations) {
        Subscription sub = mDeleteConversations.execute(new ArrayList<>(conversations))
            .observeOn(AndroidSchedulers.mainThread()).subscribe();
        manage(sub);
    }

    @Override
    public void onMoreConversationsRequired() {
        if (mLastLoadResult != null && mLastLoadResult.canMoveBackwards()) {
            loadOlderConversations();
        }
    }

    protected void reloadConversations() {
        loadConversations(mLoadConversations.all());
    }

    protected void loadOlderConversations() {
        loadConversations(mLoadConversations.older());
    }

    protected void loadNewerConversations() {
        loadConversations(mLoadConversations.newer());
    }

    private void loadConversations(@NonNull Observable<LoadResult<C>> o) {
        mLastLoadResult = null;
        manage(o
            .compose(ScheduleOn.io())
            .subscribe(this::onConversationsLoaded, this::onFatalError));
    }

    /**
     * By default displays the current conversations in the view, this method can be updated to modify the conversations list before it is
     * shown, can be used to filter the list or augment it
     */
    protected void onShowConversations(@NonNull List<C> conversations) {
        if (DEBUG) {
            Log.d(TAG, "Showing " + conversations.size() + " conversations");
        }
        mView.showConversations(new ArrayList<>(conversations)); // Create a new list to avoid issues with the immutable list emitted by the observable
    }

    /**
     * Display a fatal error
     */
    protected void onFatalError(Throwable throwable) {
        Log.e(TAG, "Fatal error", throwable);
        mView.showError(true, throwable);
    }

    private void onConversationsLoaded(LoadResult<C> result) {
        if (DEBUG) {
            Log.d(TAG, "Loaded conversations, moveForward:" + result.canMoveForwards());
        }
        mLastLoadResult = result;
        onShowConversations(result.getConversations());
        if (result.canMoveForwards()) {
            loadNewerConversations();
        }
    }
}

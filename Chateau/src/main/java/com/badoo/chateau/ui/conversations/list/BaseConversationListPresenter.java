package com.badoo.chateau.ui.conversations.list;

import android.support.annotation.NonNull;
import android.util.Log;

import com.badoo.barf.mvp.BaseRxPresenter;
import com.badoo.barf.rx.ScheduleOn;
import com.badoo.chateau.core.model.Conversation;
import com.badoo.chateau.core.usecases.conversations.DeleteConversations;
import com.badoo.chateau.core.usecases.conversations.LoadMyConversations;
import com.badoo.chateau.core.usecases.conversations.SubscribeToConversations;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;

public class BaseConversationListPresenter<C extends Conversation> extends BaseRxPresenter implements ConversationListPresenter<C> {

    private static final String TAG = BaseConversationListPresenter.class.getSimpleName();

    @NonNull
    private final ConversationListView<C> mView;
    @NonNull
    private final ConversationListFlowListener<C> mFlowListener;
    @NonNull
    private final LoadMyConversations mLoadMyConversations;
    @NonNull
    private final SubscribeToConversations<C> mSubscribeToConversations;
    @NonNull
    private final DeleteConversations mDeleteConversations;

    public BaseConversationListPresenter(@NonNull ConversationListView<C> view,
                                         @NonNull ConversationListFlowListener<C> flowListener,
                                         @NonNull LoadMyConversations loadMyConversations,
                                         @NonNull SubscribeToConversations<C> subscribeToConversations,
                                         @NonNull DeleteConversations deleteConversations) {
        mView = view;
        mFlowListener = flowListener;
        mLoadMyConversations = loadMyConversations;
        mSubscribeToConversations = subscribeToConversations;
        mDeleteConversations = deleteConversations;
    }

    @Override
    public void onStart() {
        super.onStart();
        trackSubscription(mLoadMyConversations.execute()
            .compose(ScheduleOn.io())
            .subscribe(__ -> {}, this::onFatalError));
        trackSubscription(mSubscribeToConversations.execute()
            .compose(ScheduleOn.io())
            .subscribe(this::onConversationsUpdated, this::onFatalError));
    }

    @Override
    public void onConversationClicked(@NonNull C conversation) {
        mFlowListener.requestOpenConversation(conversation);
    }

    @Override
    public void onDeleteConversations(@NonNull List<C> conversations) {
        Subscription sub = mDeleteConversations.execute(new ArrayList<>(conversations)).subscribe();
        trackSubscription(sub);
    }

    /**
     * By default displays the current conversations in the view, this method can be updated to modify the conversations list before it is
     * shown, can be used to filter the list or augment it
     */
    protected void onConversationsUpdated(@NonNull List<C> conversations) {
        Log.d(TAG, "Showing " + conversations.size() + " conversations");
        mView.showConversations(new ArrayList<>(conversations)); // Create a new list to avoid issues with the immutable list emmited by the observable
    }

    /**
     * Display a fatal error
     */
    protected void onFatalError(Throwable throwable) {
        Log.e(TAG, "Fatal error", throwable);
        mView.showError(true, throwable);
    }
}

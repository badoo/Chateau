package com.badoo.chateau.ui.conversations.list;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.badoo.barf.usecase.UseCase;
import com.badoo.barf.mvp.BasePresenter;
import com.badoo.chateau.data.models.BaseConversation;
import com.badoo.chateau.core.usecases.conversations.DeleteConversations;
import com.badoo.chateau.core.usecases.conversations.GetMyConversations;
import com.badoo.chateau.core.usecases.conversations.SubscribeToUpdatedConversations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Subscription;

public class ConversationListPresenterImpl extends BasePresenter<ConversationListPresenter.ConversationListView, ConversationListPresenter.ConversationListFlowListener> implements ConversationListPresenter {

    private static final String TAG = ConversationListPresenterImpl.class.getSimpleName();
    @NonNull
    private final GetMyConversations mGetMyConversations;
    @NonNull
    private final SubscribeToUpdatedConversations mSubscribeToUpdatedConversations;
    @NonNull
    private final DeleteConversations mDeleteConversations;

    public ConversationListPresenterImpl() {
        this(new GetMyConversations(), new SubscribeToUpdatedConversations(), new DeleteConversations());
    }

    @VisibleForTesting
    ConversationListPresenterImpl(@NonNull GetMyConversations getMyConversations,
                                  @NonNull SubscribeToUpdatedConversations subscribeToUpdatedConversations,
                                  @NonNull DeleteConversations deleteConversations) {
        mGetMyConversations = getMyConversations;
        mSubscribeToUpdatedConversations = subscribeToUpdatedConversations;
        mDeleteConversations = deleteConversations;
    }

    @Override
    public void onStart() {
        super.onStart();
        trackSubscription(mSubscribeToUpdatedConversations.execute(UseCase.NoParams.NONE)
            .map(conversation -> (BaseConversation) conversation)
            .subscribe(getView()::showConversation, this::onError));
        reloadConversations();
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Failed to load data", throwable);
        getView().showGenericError();
    }

    @Override
    public void onConversationClicked(@NonNull BaseConversation conversation) {
        getFlowListener().openConversation(conversation);
    }

    @Override
    public void onDeleteConversations(@NonNull List<BaseConversation> conversations) {
        Subscription sub = mDeleteConversations.execute(new DeleteConversations.DeleteConversationsParams(new ArrayList<>(conversations)))
            .flatMap(Observable::from)
            .map(conversation -> (BaseConversation) conversation)
            .toList()
            .subscribe(getView()::removeConversations);
        trackSubscription(sub);
    }

    private void reloadConversations() {
        getView().showLoading();
        trackSubscription(mGetMyConversations.execute(UseCase.NoParams.NONE)
            .flatMap(Observable::from)
            .map(conversation -> (BaseConversation) conversation)
            .toList()
            .subscribe(this::onConversationsLoaded, this::onError));
    }

    private void onConversationsLoaded(@NonNull List<BaseConversation> conversations) {
        Collections.sort(conversations, new ConversationByLastMessageComparator());
        getView().hideLoading();
        getView().showConversations(conversations);
    }

}

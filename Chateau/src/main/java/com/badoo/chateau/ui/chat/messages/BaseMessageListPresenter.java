package com.badoo.chateau.ui.chat.messages;

import android.support.annotation.NonNull;
import android.util.Log;

import com.badoo.barf.mvp.BaseRxPresenter;
import com.badoo.barf.rx.ScheduleOn;
import com.badoo.chateau.core.model.Message;
import com.badoo.chateau.core.repos.messages.MessageDataSource.Update;
import com.badoo.chateau.core.usecases.conversations.MarkConversationRead;
import com.badoo.chateau.core.usecases.messages.LoadMessages;
import com.badoo.chateau.core.usecases.messages.SendMessage;
import com.badoo.chateau.core.usecases.messages.SubscribeToMessageUpdates;

import java.util.Collections;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;

import static com.badoo.chateau.core.repos.messages.MessageDataSource.Update.Action;

public class BaseMessageListPresenter<M extends Message>
    extends BaseRxPresenter implements MessageListPresenter<M> {

    private static final String TAG = BaseMessageListPresenter.class.getSimpleName();

    @NonNull
    private final String mConversationId;
    @NonNull
    private final MessageListView<M> mView;

    private boolean mCanLoadOlder = true;

    // Use cases
    @NonNull
    private LoadMessages<M> mLoadMessages;
    @NonNull
    private final SubscribeToMessageUpdates<M> mSubscribeToMessageUpdates;
    @NonNull
    private final MarkConversationRead mMarkConversationRead;
    @NonNull
    private final SendMessage<M> mSendMessage;

    public BaseMessageListPresenter(@NonNull String conversationId,
                                    @NonNull MessageListView<M> view,
                                    @NonNull LoadMessages<M> loadMessages,
                                    @NonNull SubscribeToMessageUpdates<M> subscribeToMessageUpdates,
                                    @NonNull MarkConversationRead markConversationRead,
                                    @NonNull SendMessage<M> sendMessage) {
        mConversationId = conversationId;
        mView = view;
        mLoadMessages = loadMessages;
        mSubscribeToMessageUpdates = subscribeToMessageUpdates;
        mMarkConversationRead = markConversationRead;
        mSendMessage = sendMessage;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Setup subscriptions
        manage(mSubscribeToMessageUpdates.forConversation(mConversationId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onUpdate, this::onNonFatalError));
        // Request data
        reload();
    }

    private void onUpdate(Update<M> update) {
        if (update.getAction() == Action.ADDED) {
            onNewerLoaded(Collections.singletonList(update.getNewMessage()));
        }
        else if (update.getAction() == Action.UPDATED) {
            onReplace(update.getOldMessage(), update.getNewMessage());
        }
        else if (update.getAction() == Action.INVALIDATE_ALL) {
            reload();
        }
    }

    @Override
    public void onMoreMessagesRequired() {
        if (mCanLoadOlder) {
            loadOlder();
        }
    }

    @Override
    public void onResendClicked(@NonNull M message) {
        manage(mSendMessage.execute(mConversationId, message).subscribe());
    }

    protected void reload() {
        // We need to put this in some point if the view is empty.  But if it's shown even for a second and then hidden when coming
        // back it fucks up the transition animation
        //mView.showLoadingMoreMessages(true);
        manage(mLoadMessages.all(mConversationId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(result -> {
                mView.showLoadingMoreMessages(false);
                mCanLoadOlder = result.canLoadOlder();
                onReloaded(result.getMessages());
                if (result.canLoadNewer()) {
                    loadNewer();
                }
            }, this::onFatalError));
    }

    protected void onReloaded(List<M> messages) {
        markConversationRead();
        mView.showMessages(messages);
    }

    protected void loadNewer() {
        manage(mLoadMessages.newer(mConversationId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(result -> {
                onNewerLoaded(result.getMessages());
                if (result.canLoadNewer()) {
                    loadNewer();
                }
            }, this::onFatalError));
    }

    protected void onNewerLoaded(List<M> messages) {
        markConversationRead();
        mView.showNewerMessages(messages);
    }

    protected void loadOlder() {
        mCanLoadOlder = false;
        mView.showLoadingMoreMessages(true);
        manage(mLoadMessages.older(mConversationId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(result -> {
                mCanLoadOlder = result.canLoadOlder();
                mView.showLoadingMoreMessages(false);
                onOlderLoaded(result.getMessages());
            }, this::onFatalError));
    }

    protected void onOlderLoaded(List<M> messages) {
        mView.showOlderMessages(messages);
    }

    protected void onReplace(@NonNull M oldMessage, @NonNull M newMessage) {
        mView.replaceMessage(oldMessage, newMessage);
    }

    private void markConversationRead() {
        manage(mMarkConversationRead.execute(mConversationId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(conversation -> {
            }, this::onNonFatalError));
    }

    private void onFatalError(Throwable throwable) {
        Log.e(TAG, "Fatal error", throwable);
        mView.showError(true, throwable);
    }

    private void onNonFatalError(Throwable throwable) {
        Log.w(TAG, "Non-fatal error", throwable);
        mView.showError(false, throwable);
    }

}

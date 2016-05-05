package com.badoo.chateau.ui.chat.messages;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.badoo.barf.mvp.BaseRxPresenter;
import com.badoo.barf.rx.ScheduleOn;
import com.badoo.chateau.core.model.Conversation;
import com.badoo.chateau.core.model.Message;
import com.badoo.chateau.core.model.User;
import com.badoo.chateau.core.usecases.conversations.GetConversation;
import com.badoo.chateau.core.usecases.conversations.MarkConversationRead;
import com.badoo.chateau.core.usecases.istyping.SubscribeToUsersTyping;
import com.badoo.chateau.core.usecases.messages.LoadMessages;
import com.badoo.chateau.core.usecases.messages.SubscribeToMessages;

import java.util.List;

import rx.Subscription;

public class BaseMessageListPresenter<M extends Message, C extends Conversation, U extends User>
    extends BaseRxPresenter implements MessageListPresenter {

    private static final String TAG = BaseMessageListPresenter.class.getSimpleName();

    @NonNull
    private final String mChatId;
    @NonNull
    private final MessageListView<M, C> mView;
    @NonNull
    private final MessageListFlowListener mFlowListener;
    @Nullable
    private M mOldestMessage = null;
    private boolean mCanLoadMore = true;

    // Use cases
    @NonNull
    private LoadMessages<M> mLoadMessages;
    @NonNull
    private final SubscribeToMessages<M> mSubscribeToMessages;
    @NonNull
    private final MarkConversationRead mMarkConversationRead;
    @NonNull
    private final GetConversation<C> mGetConversation;
    @NonNull
    private final SubscribeToUsersTyping<U> mSubscribeToUsersTyping;

    public BaseMessageListPresenter(@NonNull String chatId,
                                    @NonNull MessageListView<M, C> view,
                                    @NonNull MessageListFlowListener flowListener,
                                    @NonNull LoadMessages<M> loadMessages,
                                    @NonNull SubscribeToMessages<M> subscribeToMessages,
                                    @NonNull MarkConversationRead markConversationRead,
                                    @NonNull GetConversation<C> getConversation,
                                    @NonNull SubscribeToUsersTyping<U> subscribeToUsersTyping) {
        mChatId = chatId;
        mView = view;
        mFlowListener = flowListener;
        mLoadMessages = loadMessages;
        mSubscribeToMessages = subscribeToMessages;
        mMarkConversationRead = markConversationRead;
        mGetConversation = getConversation;
        mSubscribeToUsersTyping = subscribeToUsersTyping;
    }

    @Override
    public void onCreate() {
        final Subscription getConversationSub = mGetConversation.execute(mChatId)
            .compose(ScheduleOn.io())
            .subscribe(mView::showConversation, this::onFatalError);

        final Subscription messagesSub = mSubscribeToMessages.execute(mChatId)
            .compose(ScheduleOn.io())
            .subscribe(this::onMessages, this::onNonFatalError);

        final Subscription otherUserTypingSub = mSubscribeToUsersTyping.execute(mChatId)
            .compose(ScheduleOn.io())
            .subscribe((user) -> {
                mView.showOtherUserTyping();
            }, this::onNonFatalError);

        trackSubscription(messagesSub);
        trackSubscription(getConversationSub);
        trackSubscription(otherUserTypingSub);

        loadChatMessages(null);
        markConversationRead();
    }

    private void onMessages(List<M> messages) {
        if (!messages.isEmpty()) {
            mOldestMessage = messages.get(0);
        }
        mView.showMessages(messages);
        markConversationRead();
    }

    @Override
    public void onMoreMessagesRequired() {
        if (!mCanLoadMore) {
            return;
        }
        loadChatMessages(mOldestMessage);
    }

    @Override
    public void onImageClicked(@NonNull Uri uri) {
        mFlowListener.requestOpenImage(uri);
    }

    private void loadChatMessages(@Nullable M lastMessage) {
        mView.showLoadingMoreMessages(true);
        Subscription sub = mLoadMessages.execute(mChatId, lastMessage)
            .compose(ScheduleOn.io())
            .subscribe(hasMoreData -> {
                mCanLoadMore = hasMoreData;
                mView.showLoadingMoreMessages(false);
            }, this::onFatalError);
        trackSubscription(sub);
    }

    private void markConversationRead() {
        trackSubscription(mMarkConversationRead.execute(mChatId)
            .compose(ScheduleOn.io())
            .subscribe(conversation -> { }, this::onNonFatalError));
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

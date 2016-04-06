package com.badoo.chateau.ui.chat.messages;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.badoo.barf.mvp.BasePresenter;
import com.badoo.chateau.core.usecases.conversations.GetConversation;
import com.badoo.chateau.core.usecases.conversations.MarkConversationRead;
import com.badoo.chateau.core.usecases.istyping.SubscribeToUsersTyping;
import com.badoo.chateau.core.usecases.messages.ChatParams;
import com.badoo.chateau.core.usecases.messages.GetChatMessages;
import com.badoo.chateau.core.usecases.messages.SubscribeToNewMessages;
import com.badoo.chateau.core.usecases.messages.SubscribeToUpdatedMessages;
import com.badoo.chateau.data.models.BaseConversation;
import com.badoo.chateau.data.models.BaseMessage;

import rx.Observable;
import rx.Subscription;

import static com.badoo.chateau.core.usecases.messages.GetChatMessages.GetChatMessagesParams;

public class MessageListPresenterImpl extends BasePresenter<MessageListView, MessageListPresenter.MessageListFlowListener> implements MessageListPresenter {

    private static final String TAG = MessageListPresenterImpl.class.getSimpleName();

    @NonNull
    private final String mChatId;
    @NonNull
    private GetChatMessages mGetChatMessages;
    @NonNull
    private final SubscribeToNewMessages mSubscribeToNewMessages;
    @NonNull
    private final SubscribeToUpdatedMessages mSubscribeToUpdatedMessages;
    @NonNull
    private final MarkConversationRead mMarkConversationRead;
    @NonNull
    private final GetConversation mGetConversation;
    @NonNull
    private final SubscribeToUsersTyping mSubscribeToUsersTyping;
    @Nullable
    private Subscription mGetMoreMessagesSub;
    private BaseMessage mOldestMessage = null;
    private boolean mCanLoadMore = true;

    public MessageListPresenterImpl(@NonNull String chatId) {
        this(chatId, new GetChatMessages(), new SubscribeToNewMessages(), new SubscribeToUpdatedMessages(), new MarkConversationRead(),
            new GetConversation(), new SubscribeToUsersTyping());
    }

    @VisibleForTesting
    MessageListPresenterImpl(@NonNull String chatId,
                             @NonNull GetChatMessages getChatMessages,
                             @NonNull SubscribeToNewMessages subscribeToNewMessages,
                             @NonNull SubscribeToUpdatedMessages subscribeToUpdatedMessages,
                             @NonNull MarkConversationRead markConversationRead,
                             @NonNull GetConversation getConversation,
                             @NonNull SubscribeToUsersTyping subscribeToUsersTyping) {
        mChatId = chatId;
        mGetChatMessages = getChatMessages;
        mSubscribeToNewMessages = subscribeToNewMessages;
        mSubscribeToUpdatedMessages = subscribeToUpdatedMessages;
        mMarkConversationRead = markConversationRead;
        mGetConversation = getConversation;
        mSubscribeToUsersTyping = subscribeToUsersTyping;
    }

    @Override
    public void onCreate() {
        final Subscription getConversationSub = mGetConversation.execute(new ChatParams(mChatId))
            .map(conversation -> (BaseConversation) conversation)
            .map(BaseConversation::getName)
            .subscribe(getView()::setTitle, this::onError);

        getChatMessages(mGetChatMessages, mOldestMessage);

        final Subscription newMessagesSub = mSubscribeToNewMessages.execute(new ChatParams(mChatId))
            .map(message -> (BaseMessage) message)
            .subscribe((message) -> {
                getView().showMessage(message);
                markConversationRead();
            }, this::onError);

        final Subscription updatedMessagesSub = mSubscribeToUpdatedMessages.execute(new ChatParams(mChatId))
            .map(message -> (BaseMessage) message)
            .subscribe((message) -> {
                getView().replaceMessage(message);
                markConversationRead();
            }, this::onError);

        final Subscription otherUserTypingSub = mSubscribeToUsersTyping.execute(new ChatParams(mChatId))
            .subscribe((user) -> {
                getView().showOtherUserTyping();
            }, this::onError);

        trackSubscription(newMessagesSub);
        trackSubscription(updatedMessagesSub);
        trackSubscription(getConversationSub);
        trackSubscription(otherUserTypingSub);

        markConversationRead();
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Failed to load data", throwable);
        getView().showGenericError();
    }

    @Override
    public void destroy() {
        if (mGetMoreMessagesSub != null && !mGetMoreMessagesSub.isUnsubscribed()) {
            mGetMoreMessagesSub.unsubscribe();
        }
        super.destroy();
    }

    private void getChatMessages(@NonNull GetChatMessages getChatMessages, @Nullable BaseMessage lastMessage) {
        getView().showLoadingMoreMessages(true);
        mGetMoreMessagesSub = getChatMessages.execute(new GetChatMessagesParams(mChatId, lastMessage))
            .flatMap(Observable::from)
            .map(message -> (BaseMessage) message)
            .toList()
            .subscribe(baseMessages -> {
                mCanLoadMore = !baseMessages.isEmpty();
                if (baseMessages.isEmpty()) {
                    mOldestMessage = null;
                }
                else {
                    mOldestMessage = baseMessages.get(0);
                    getView().showMessages(baseMessages);
                }
                getView().showLoadingMoreMessages(false);
            }, this::onError);
    }

    private void markConversationRead() {
        trackSubscription(mMarkConversationRead.execute(new ChatParams(mChatId)).subscribe(conversation -> {
        }, this::onError));
    }

    @Override
    public void onMoreMessagesRequired() {
        if (mGetMoreMessagesSub != null && !mGetMoreMessagesSub.isUnsubscribed() || !mCanLoadMore) {
            return;
        }
        getChatMessages(mGetChatMessages, mOldestMessage);
    }

    @Override
    public void onImageClicked(@NonNull Uri uri) {
        getFlowListener().openImage(uri);
    }

}

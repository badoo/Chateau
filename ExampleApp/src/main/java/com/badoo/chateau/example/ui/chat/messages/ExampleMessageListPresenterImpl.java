package com.badoo.chateau.example.ui.chat.messages;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.badoo.chateau.core.usecases.conversations.MarkConversationRead;
import com.badoo.chateau.core.usecases.messages.LoadMessages;
import com.badoo.chateau.core.usecases.messages.SendMessage;
import com.badoo.chateau.core.usecases.messages.SubscribeToMessageUpdates;
import com.badoo.chateau.example.data.model.ExampleMessage;
import com.badoo.chateau.ui.chat.messages.BaseMessageListPresenter;

/**
 * Extension of BaseMessageListPresenter for the example app
 */
public class ExampleMessageListPresenterImpl extends BaseMessageListPresenter<ExampleMessage> implements ExampleMessageListPresenter {

    @NonNull
    private final ExampleMessageListFlowListener mFlowListener;

    public ExampleMessageListPresenterImpl(@NonNull String conversationId,
                                           @NonNull ExampleMessageListView view,
                                           @NonNull ExampleMessageListFlowListener flowListener,
                                           @NonNull LoadMessages<ExampleMessage> loadMessages,
                                           @NonNull SubscribeToMessageUpdates<ExampleMessage> subscribeToMessageUpdates,
                                           @NonNull MarkConversationRead markConversationRead,
                                           @NonNull SendMessage<ExampleMessage> sendMessage) {
        super(conversationId, view, loadMessages, subscribeToMessageUpdates, markConversationRead, sendMessage);

        mFlowListener = flowListener;
    }

    @Override
    public void onImageClicked(@NonNull Uri uri) {
        mFlowListener.requestOpenImage(uri);
    }
}

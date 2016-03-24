package com.badoo.chateau.ui.chat.messages;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.badoo.barf.mvp.Presenter;

/**
 * Presenter for the list of messages in a chat. Handles request to load messages as well as interactions with the items in the list.
 */
public interface MessageListPresenter extends Presenter<MessageListView, MessageListPresenter.MessageListFlowListener> {

    /**
     * Notifies that the more messages are required for display if possible as the top of the
     * list has been reached.
     */
    void onMoreMessagesRequired();

    /**
     * Notifies that an image has been clicked.
     */
    void onImageClicked(@NonNull Uri uri);

    interface MessageListFlowListener extends Presenter.FlowListener {

        void openImage(@NonNull Uri imageUri);
    }
}

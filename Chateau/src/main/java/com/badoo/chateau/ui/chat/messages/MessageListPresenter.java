package com.badoo.chateau.ui.chat.messages;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.badoo.barf.mvp.Presenter;
import com.badoo.barf.mvp.View;
import com.badoo.chateau.data.models.BaseMessage;

import java.util.List;

/**
 * Presenter for the list of messages in a chat. Handles request to load messages as well as interactions with the items in the list.
 */
public interface MessageListPresenter extends Presenter<MessageListPresenter.MessageListView, MessageListPresenter.MessageListFlowListener> {

    /**
     * Notifies that the more messages are required for display if possible as the top of the
     * list has been reached.
     */
    void onMoreMessagesRequired();

    /**
     * Notifies that an image has been clicked.
     */
    void onImageClicked(@NonNull Uri uri);

    interface MessageListView extends View<MessageListPresenter> {

        /**
         * Set the title of the message list
         */
        void setTitle(@NonNull String title);

        /**
         * Show that the other user is typing.
         */
        void showOtherUserTyping();

        /**
         * Show that previous messages are been loaded.
         */
        void showLoadingMoreMessages(boolean show);

        /**
         * Show a given message.  This will update a message if a matching message is already been displayed.
         */
        void showMessage(@NonNull BaseMessage message);

        /**
         * Show all the given messages.  It is a assumed that these messages make up a single chunk and can be inserted together.  If not,
         * {@link #showMessage(BaseMessage)} should be called multiple times.
         */
        void showMessages(@NonNull List<BaseMessage> messages);

        /**
         * Replace a message with the same id with this message.  If a message with the same id is not found, then the message will not be
         * displayed.
         */
        void replaceMessage(@NonNull BaseMessage message);

        /**
         * Show an error message to the user.
         */
        void showGenericError();

    }

    interface MessageListFlowListener extends Presenter.FlowListener {

        void requestOpenImage(@NonNull Uri imageUri);
    }
}

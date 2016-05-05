package com.badoo.chateau.ui.chat.messages;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.badoo.barf.mvp.FlowListener;
import com.badoo.barf.mvp.MvpPresenter;
import com.badoo.barf.mvp.MvpView;
import com.badoo.chateau.core.model.Conversation;
import com.badoo.chateau.core.model.Message;
import com.badoo.chateau.data.models.BaseMessage;

import java.util.List;

/**
 * Presenter for the list of messages in a chat. Handles request to load messages as well as interactions with the items in the list.
 */
public interface MessageListPresenter extends MvpPresenter {

    /**
     * Notifies that the more messages are required for display if possible as the top of the
     * list has been reached.
     */
    void onMoreMessagesRequired();

    /**
     * Notifies that an image has been clicked.
     */
    void onImageClicked(@NonNull Uri uri);

    interface MessageListView<M extends Message, C extends Conversation> extends MvpView {

        /**
         * Set the title of the message list
         */
        void showConversation(@NonNull C conversation);

        /**
         * Show that the other user is typing.
         */
        void showOtherUserTyping();

        /**
         * Show that previous messages are been loaded.
         */
        void showLoadingMoreMessages(boolean show);

        /**
         * Show all the given messages.  It is a assumed that these messages make up a single chunk and can be inserted together.  If not,
         * {@link #showMessage(BaseMessage)} should be called multiple times.
         */
        void showMessages(@NonNull List<M> messages);

        /**
         * Show an error message to the user (if the error warrants it)
         *
         * @param fatal true if the error was fatal, false if it can be ignored while still maintaining some functionality.
         */
        void showError(boolean fatal, @Nullable Throwable throwable);

    }

    interface MessageListFlowListener extends FlowListener {

        void requestOpenImage(@NonNull Uri imageUri);
    }
}

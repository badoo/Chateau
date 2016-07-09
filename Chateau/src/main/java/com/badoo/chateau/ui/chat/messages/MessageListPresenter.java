package com.badoo.chateau.ui.chat.messages;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.badoo.barf.mvp.MvpPresenter;
import com.badoo.barf.mvp.MvpView;
import com.badoo.chateau.core.model.Message;
import com.badoo.chateau.data.models.BaseMessage;

import java.util.List;

/**
 * Presenter for the list of messages in a chat. Handles request to load messages as well as interactions with the items in the list.
 */
public interface MessageListPresenter<M extends Message> extends MvpPresenter {

    /**
     * Notifies that the more messages are required for display if possible as the top of the
     * list has been reached.
     */
    void onMoreMessagesRequired();

    /**
     * Notifies that a resend was requested.
     */
    void onResendClicked(@NonNull M message);

    interface MessageListView<M extends Message> extends MvpView {

        /**
         * Show that previous messages are been loaded.
         */
        void showLoadingMoreMessages(boolean show);

        /**
         * Show all the given messages.  It is a assumed that these messages make up a single chunk and can be inserted together.  If not,
         * {@link #showMessage(BaseMessage)} should be called multiple times.
         */
        void showMessages(@NonNull List<M> messages);

        void showNewerMessages(@NonNull List<M> messages);

        void showOlderMessages(@NonNull List<M> messages);

        void replaceMessage(@NonNull M oldMessage, @NonNull M newMessage);

        /**
         * Show an error message to the user (if the error warrants it)
         *
         * @param fatal true if the error was fatal, false if it can be ignored while still maintaining some functionality.
         */
        void showError(boolean fatal, @Nullable Throwable throwable);
    }
}

package com.badoo.chateau.ui.conversations.list;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.badoo.barf.mvp.FlowListener;
import com.badoo.barf.mvp.MvpPresenter;
import com.badoo.barf.mvp.MvpView;
import com.badoo.chateau.core.model.Conversation;

import java.util.List;

public interface ConversationListPresenter<C extends Conversation> extends MvpPresenter {

    /**
     * Called when a conversation is clicked.
     */
    void onConversationClicked(@NonNull C conversation);

    /**
     * Called when a number of conversations has been selected to be deleted
     */
    void onDeleteConversations(@NonNull List<C> conversations);

    interface ConversationListView<C extends Conversation> extends MvpView {

        /**
         * Display the given conversations
         */
        void showConversations(List<C> conversations);

        /**
         * Show an error message to the user (if the error warrants it)
         *
         * @param fatal true if the error was fatal, false if it can be ignored while still maintaining some functionality.
         */
        void showError(boolean fatal, @Nullable Throwable throwable);

        /**
         * Show the loading indicator.
         */
        void showLoading();

        /**
         * Hide the loading indicator.
         */
        void hideLoading();

    }

    interface ConversationListFlowListener<C extends Conversation> extends FlowListener {

        /**
         * Called when a conversation is selected to be opened.
         */
        void requestOpenConversation(@NonNull C conversation);

    }
}

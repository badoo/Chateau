package com.badoo.chateau.ui.conversations.list;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.badoo.barf.mvp.Presenter;
import com.badoo.barf.mvp.BasePresenter;
import com.badoo.barf.mvp.View;
import com.badoo.chateau.data.models.BaseConversation;

import java.util.List;

public interface ConversationListPresenter extends Presenter<ConversationListPresenter.ConversationListView, ConversationListPresenter.ConversationListFlowListener> {

    /**
     * Called when a conversation is clicked.
     */
    void onConversationClicked(@NonNull BaseConversation conversation);

    /**
     * Called when a number of conversations has been selected to be deleted
     */
    void onDeleteConversations(@NonNull List<BaseConversation> conversations);

    interface ConversationListView extends View<ConversationListPresenter> {

        /**
         * Show a conversation.  If the conversation is already been displayed then it will be updated, otherwise it will be
         * added to the displayed conversations.
         */
        void showConversation(@NonNull BaseConversation conversation);

        /**
         * Show all the conversations in the list.
         */
        void showConversations(List<BaseConversation> conversations);

        /**
         * Removes a number of conversations from the list.
         */
        void removeConversations(@NonNull List<BaseConversation> conversations);

        /**
         * Show an error message.
         */
        void showGenericError();

        /**
         * Show the loading indicator.
         */
        void showLoading();

        /**
         * Hide the loading indicator.
         */
        void hideLoading();

    }

    interface ConversationListFlowListener extends BasePresenter.FlowListener {

        /**
         * Called when a conversation is selected to be opened.
         */
        void requestOpenConversation(@NonNull BaseConversation conversation);

    }
}

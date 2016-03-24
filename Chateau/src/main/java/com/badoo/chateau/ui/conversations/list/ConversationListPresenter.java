package com.badoo.chateau.ui.conversations.list;

import android.support.annotation.NonNull;

import com.badoo.barf.mvp.Presenter;
import com.badoo.barf.mvp.BasePresenter;
import com.badoo.chateau.data.models.BaseConversation;

import java.util.List;

public interface ConversationListPresenter extends Presenter<ConversationListView, ConversationListPresenter.ConversationListFlowListener> {

    /**
     * Called when a conversation is clicked.
     */
    void onConversationClicked(@NonNull BaseConversation conversation);

    /**
     * Called when a new conversation is requested.
     */
    void onCreateNewConversationClicked();

    /**
     * Called when a number of conversations has been selected to be deleted
     */
    void onDeleteConversations(@NonNull List<BaseConversation> conversations);

    interface ConversationListFlowListener extends BasePresenter.FlowListener {

        /**
         * Called when a conversation is selected to be opened.
         */
        void openConversation(@NonNull BaseConversation conversation);

        /**
         * Called when a new conversation is required.
         */
        void createNewConversation();
    }
}

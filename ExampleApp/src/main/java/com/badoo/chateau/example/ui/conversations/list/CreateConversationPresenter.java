package com.badoo.chateau.example.ui.conversations.list;

import com.badoo.barf.mvp.Presenter;
import com.badoo.barf.mvp.View;

/**
 * Component presenter for handling creating new conversations (this is not handled by the base ConversationListPresenter as this is often
 * application dependent).
 */
public interface CreateConversationPresenter extends Presenter<CreateConversationPresenter.CreateConversationView, CreateConversationPresenter.CreateConversationFlowListener> {

    /**
     * Called when a new conversation is requested.
     */
    void onCreateNewConversationClicked();

    interface CreateConversationFlowListener extends Presenter.FlowListener {

        /**
         * Called when a new conversation is required.
         */
        void requestCreateNewConversation();
    }

    interface CreateConversationView extends View<CreateConversationPresenter> {

    }
}

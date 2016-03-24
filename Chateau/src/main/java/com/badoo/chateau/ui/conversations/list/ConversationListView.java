package com.badoo.chateau.ui.conversations.list;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.badoo.barf.mvp.View;
import com.badoo.chateau.data.models.BaseConversation;

import java.util.List;

public interface ConversationListView extends View<ConversationListPresenter> {

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
    void removeConversations(@NonNull  List<BaseConversation> conversations);

    /**
     * Show an error message.
     */
    void showError(@StringRes int messageResourceId);

    /**
     * Show the loading indicator.
     */
    void showLoading();

    /**
     * Hide the loading indicator.
     */
    void hideLoading();

}

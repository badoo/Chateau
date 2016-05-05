package com.badoo.chateau.ui.conversations.create.namegroup;

import android.support.annotation.NonNull;

import com.badoo.barf.mvp.FlowListener;
import com.badoo.barf.mvp.MvpPresenter;
import com.badoo.barf.mvp.MvpView;
import com.badoo.chateau.core.model.Conversation;

/**
 * Presenter which manages naming of a new chat group
 */
public interface NameGroupPresenter extends MvpPresenter {

    /**
     * Called when a name has been selected
     */
    void onCreateGroupClicked(@NonNull String name);

    interface NameGroupFlowListener<C extends Conversation> extends FlowListener {

        /**
         * Requests a conversation to be opened
         */
        void requestOpenChat(@NonNull C conversationId);
    }

    interface NameGroupView extends MvpView {

        void showGroupNameEmptyError();

        void clearErrors();

    }
}

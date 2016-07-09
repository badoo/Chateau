package com.badoo.chateau.ui.chat.info;

import android.support.annotation.NonNull;

import com.badoo.barf.mvp.MvpPresenter;
import com.badoo.barf.mvp.MvpView;
import com.badoo.chateau.core.model.Conversation;

/**
 * Presenter for handling information about a conversation other than the messages in it.
 */
public interface ChatInfoPresenter<C extends Conversation> extends MvpPresenter {

    interface ChatInfoView<C extends Conversation> extends MvpView {

        /**
         * Show information about the conversation
         */
        void showConversation(@NonNull C conversation);

    }
}

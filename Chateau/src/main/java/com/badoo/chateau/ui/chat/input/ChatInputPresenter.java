package com.badoo.chateau.ui.chat.input;

import android.support.annotation.NonNull;

import com.badoo.barf.mvp.MvpPresenter;
import com.badoo.barf.mvp.MvpView;
import com.badoo.chateau.core.model.Message;

/**
 * Presenter managing the text input part of the chat screen
 */
public interface ChatInputPresenter<M extends Message> extends MvpPresenter {

    /**
     * Called when a message has been entered and is ready to be sent
     */
    void onSendMessage(@NonNull M message);

    interface ChatInputView extends MvpView {

        /**
         * Clears the enter text in the text field
         */
        void clearText();

    }


}

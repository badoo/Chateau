package com.badoo.chateau.ui.chat.typing;

import android.support.annotation.NonNull;

import com.badoo.barf.mvp.MvpPresenter;
import com.badoo.barf.mvp.MvpView;
import com.badoo.chateau.core.model.User;

/**
 * Presenter for handling the "is writing" statuses (for both the local and remote users)
 */
public interface IsTypingPresenter<U extends User> extends MvpPresenter {

    /**
     * Called when the user is typing a message, should be throttled (don't call for every keystroke)
     */
    void onUserTyping();

    interface IsTypingView<U extends User> extends MvpView {

        void showOtherUserTyping(@NonNull U user);

    }

}

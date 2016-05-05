package com.badoo.chateau.ui.conversations.create.selectusers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.badoo.barf.mvp.FlowListener;
import com.badoo.barf.mvp.MvpPresenter;
import com.badoo.barf.mvp.MvpView;
import com.badoo.chateau.core.model.Conversation;
import com.badoo.chateau.core.model.User;

import java.util.List;

public interface UserListPresenter<U extends User> extends MvpPresenter {

    /**
     * To be invoked when the final selection of users has been made.
     */
    void onUsersSelected(List<U> userIds);

    interface UserListView<U extends User> extends MvpView {

        void showUsers(@NonNull List<U> users);

        /**
         * Show an error message to the user (if the error warrants it)
         *
         * @param fatal true if the error was fatal, false if it can be ignored while still maintaining some functionality.
         */
        void showError(boolean fatal, @Nullable Throwable throwable);

    }

    interface UserListFlowListener<C extends Conversation, U extends User> extends FlowListener {

        void requestOpenChat(@NonNull C conversation);

        void requestCreateGroupChat(@NonNull List<U> users);
    }
}

package com.badoo.chateau.ui.conversations.create.selectusers;

import android.support.annotation.NonNull;

import com.badoo.barf.mvp.Presenter;
import com.badoo.chateau.data.models.BaseUser;

import java.util.List;

public interface UserListPresenter extends Presenter<UserListView, UserListPresenter.UserListFlowListener> {

    void onUsersSelected(List<BaseUser> user);

    interface UserListFlowListener extends Presenter.FlowListener {

        void openChat(@NonNull String chatId);

        void createGroupChat(@NonNull List<BaseUser> users);
    }
}

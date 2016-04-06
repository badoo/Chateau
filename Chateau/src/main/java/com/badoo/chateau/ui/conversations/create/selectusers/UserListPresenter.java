package com.badoo.chateau.ui.conversations.create.selectusers;

import android.support.annotation.NonNull;

import com.badoo.barf.mvp.Presenter;
import com.badoo.barf.mvp.View;
import com.badoo.chateau.data.models.BaseUser;

import java.util.List;

public interface UserListPresenter extends Presenter<UserListPresenter.UserListView, UserListPresenter.UserListFlowListener> {

    void onUsersSelected(List<BaseUser> user);

    interface UserListView extends View<UserListPresenter> {

        void showUsers(@NonNull List<BaseUser> users);

        void showGenericError();

    }

    interface UserListFlowListener extends Presenter.FlowListener {

        void requestOpenChat(@NonNull String chatId);

        void requestCreateGroupChat(@NonNull List<BaseUser> users);
    }
}

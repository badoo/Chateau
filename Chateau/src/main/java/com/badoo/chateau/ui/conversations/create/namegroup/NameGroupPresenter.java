package com.badoo.chateau.ui.conversations.create.namegroup;

import android.support.annotation.NonNull;

import com.badoo.barf.mvp.Presenter;
import com.badoo.barf.mvp.View;

public interface NameGroupPresenter extends Presenter<NameGroupPresenter.NameGroupView, NameGroupPresenter.NameGroupFlowListener> {

    void onCreateGroupClicked(@NonNull String name);

    interface NameGroupFlowListener extends Presenter.FlowListener {

        void requestOpenChat(@NonNull String chatId);
    }

    interface NameGroupView extends View<NameGroupPresenter> {

        void showGroupNameEmptyError();

        void clearErrors();

    }
}

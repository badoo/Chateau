package com.badoo.chateau.ui.conversations.create.namegroup;

import android.support.annotation.NonNull;

import com.badoo.barf.mvp.Presenter;

public interface NameGroupPresenter extends Presenter<NameGroupView, NameGroupPresenter.NameGroupFlowListener> {

    void onCreateGroupClicked(@NonNull String name);

    interface NameGroupFlowListener extends Presenter.FlowListener {

        void openChat(@NonNull String chatId);
    }
}

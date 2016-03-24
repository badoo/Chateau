package com.badoo.chateau.ui.conversations.create.namegroup;

import com.badoo.barf.mvp.View;

public interface NameGroupView extends View<NameGroupPresenter> {

    void showGroupNameEmptyError();

    void clearErrors();

}

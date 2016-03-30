package com.badoo.chateau.ui.conversations.create.selectusers;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.badoo.barf.mvp.View;
import com.badoo.chateau.data.models.BaseUser;

import java.util.List;

public interface UserListView extends View<UserListPresenter> {

    void showUsers(@NonNull List<BaseUser> users);

    void showError(@StringRes int errorMessage);

}

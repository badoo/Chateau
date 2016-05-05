package com.badoo.chateau.example.ui.conversations.create.namegroup;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;

import com.badoo.barf.mvp.MvpView;
import com.badoo.barf.mvp.PresenterFactory;
import com.badoo.chateau.example.R;
import com.badoo.chateau.extras.ViewFinder;
import com.badoo.chateau.ui.conversations.create.namegroup.NameGroupPresenter;
import com.badoo.chateau.ui.conversations.create.namegroup.NameGroupPresenter.NameGroupView;


class NameGroupViewImpl implements NameGroupView, View.OnClickListener, MvpView {

    private final TextInputLayout mGroupName;
    @NonNull
    private final NameGroupPresenter mPresenter;

    NameGroupViewImpl(@NonNull ViewFinder viewFinder,
                      @NonNull PresenterFactory<NameGroupView, NameGroupPresenter> presenterFactory) {
        mPresenter = presenterFactory.init(this);

        mGroupName = viewFinder.findViewById(R.id.nameGroup_groupName);
        Button createGroup = viewFinder.findViewById(R.id.nameGroup_createGroup);
        createGroup.setOnClickListener(this);
    }

    @Override
    public void showGroupNameEmptyError() {
        mGroupName.setError(mGroupName.getResources().getString(R.string.error_field_required));
    }

    @Override
    public void clearErrors() {
        mGroupName.setErrorEnabled(false);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.nameGroup_createGroup) {
            //noinspection ConstantConditions
            mPresenter.onCreateGroupClicked(mGroupName.getEditText().getText().toString());
        }
    }
}

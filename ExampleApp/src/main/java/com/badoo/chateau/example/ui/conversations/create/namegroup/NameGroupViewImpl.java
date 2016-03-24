package com.badoo.chateau.example.ui.conversations.create.namegroup;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;

import com.badoo.barf.mvp.BaseView;
import com.badoo.chateau.example.R;
import com.badoo.chateau.ui.conversations.create.namegroup.NameGroupPresenter;
import com.badoo.chateau.ui.conversations.create.namegroup.NameGroupView;
import com.badoo.chateau.example.ui.util.ViewFinder;


public class NameGroupViewImpl extends BaseView<NameGroupPresenter> implements NameGroupView, View.OnClickListener {

    private final TextInputLayout mGroupName;

    NameGroupViewImpl(@NonNull ViewFinder viewFinder) {
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
            getPresenter().onCreateGroupClicked(mGroupName.getEditText().getText().toString());
        }
    }
}

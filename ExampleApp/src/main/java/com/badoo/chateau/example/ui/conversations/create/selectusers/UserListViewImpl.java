package com.badoo.chateau.example.ui.conversations.create.selectusers;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.badoo.barf.mvp.BaseView;
import com.badoo.chateau.example.R;
import com.badoo.chateau.data.models.BaseUser;
import com.badoo.chateau.example.ui.BaseActivity;
import com.badoo.chateau.example.ui.util.BindableViewHolder;
import com.badoo.chateau.extras.MultiSelectionHelper;
import com.badoo.chateau.extras.ViewFinder;
import com.badoo.chateau.ui.conversations.create.selectusers.UserListPresenter;
import com.badoo.chateau.ui.conversations.create.selectusers.UserListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.view.View.OnLongClickListener;
import static com.badoo.chateau.extras.MultiSelectionHelper.MODE_MULTIPLE_SELECT;
import static com.badoo.chateau.extras.MultiSelectionHelper.MODE_SINGLE_SELECT;


class UserListViewImpl extends BaseView<UserListPresenter> implements UserListView, BaseActivity.BackPressedListener, MultiSelectionHelper.OnModeChangedListener {

    private final List<BaseUser> mUsers = new ArrayList<>();

    private final View mParent;
    private final RecyclerView mUsersList;
    private final FloatingActionButton mGroupAction;
    private final ContentLoadingProgressBar mProgress;

    private final MultiSelectionHelper mMultiSelectionHelper;

    UserListViewImpl(@NonNull ViewFinder viewFinder) {
        mParent = viewFinder.findViewById(R.id.createConversation_parent);

        mProgress = viewFinder.findViewById(R.id.createConversation_progress);
        mProgress.show();
        mUsersList = viewFinder.findViewById(R.id.createConversation_userList);
        mUsersList.setLayoutManager(new LinearLayoutManager(mUsersList.getContext()));
        mUsersList.setAdapter(new UsersAdapter());
        mMultiSelectionHelper = new MultiSelectionHelper(mUsersList.getAdapter(), this, null);

        mGroupAction = viewFinder.findViewById(R.id.createConversation_groupAction);
        mGroupAction.setOnClickListener(this::onGroupActionClick);
    }


    @Override
    public void showUsers(@NonNull List<BaseUser> users) {
        mProgress.hide();
        final int oldSize = mUsers.size();
        if (mUsers.addAll(users)) {
            mUsersList.getAdapter().notifyItemRangeInserted(oldSize - 1, mUsers.size() - 1);
        }
    }

    @Override
    public void showError(@StringRes int errorMessage) {
        Snackbar.make(mUsersList, errorMessage, Snackbar.LENGTH_INDEFINITE).show();
    }

    public void onGroupActionClick(View v) {
        if (mMultiSelectionHelper.getMode() == MODE_SINGLE_SELECT) {
            mMultiSelectionHelper.switchTo(MODE_MULTIPLE_SELECT);
        }
        else {
            getPresenter().onUsersSelected(getSelectedUsers());
        }
    }
    
    @Override
    public boolean onBackPressed() {
        switch (mMultiSelectionHelper.getMode()) {
            case MODE_SINGLE_SELECT:
                return false;
            case MODE_MULTIPLE_SELECT:
                mMultiSelectionHelper.clearSelectedPositions();
                return true;
        }
        return false;
    }

    @Override
    public void onModeChanged(@MultiSelectionHelper.Mode int mode) {
        switch (mode) {
            case MODE_SINGLE_SELECT:
                mGroupAction.setImageResource(R.drawable.ic_group_chat);
                break;
            case MODE_MULTIPLE_SELECT:
                mGroupAction.setImageResource(R.drawable.ic_done);
                Snackbar.make(mParent, R.string.info_select_users_for_group_chat, Snackbar.LENGTH_SHORT).show();
                break;
        }
    }

    private List<BaseUser> getSelectedUsers() {
        final List<BaseUser> users = new ArrayList<>();
        for (int position : mMultiSelectionHelper.getSelectedItems()) {
            users.add(mUsers.get(position));
        }
        return users;
    }

    class UsersAdapter extends RecyclerView.Adapter<UserViewHolder> {

        @Override
        public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new UserViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_user, parent, false));
        }

        @Override
        public void onBindViewHolder(UserViewHolder holder, int position) {
            holder.bind(mUsers.get(position));
        }

        @Override
        public int getItemCount() {
            return mUsers.size();
        }
    }

    class UserViewHolder extends BindableViewHolder<BaseUser> implements OnClickListener, OnLongClickListener {

        private View mContainer;
        private TextView mName;

        public UserViewHolder(View itemView) {
            super(itemView);
            mContainer = itemView;
            final ViewFinder finder = ViewFinder.from(itemView);
            mName = finder.findViewById(R.id.user_name);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void bind(BaseUser user) {
            super.bind(user);
            mName.setText(user.getDisplayName());
            mContainer.setSelected(mMultiSelectionHelper.isPositionSelected(mUsers.indexOf(user)));
        }

        @Override
        public void onClick(View v) {
            if (!mMultiSelectionHelper.onClick(mUsers.indexOf(getBoundItem()))) {
                getPresenter().onUsersSelected(Collections.singletonList(getBoundItem()));
            }
        }

        @Override
        public boolean onLongClick(View v) {
            return mMultiSelectionHelper.onLongClick(mUsers.indexOf(getBoundItem()));
        }
    }

}

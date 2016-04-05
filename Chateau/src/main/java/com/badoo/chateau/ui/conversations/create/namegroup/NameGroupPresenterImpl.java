package com.badoo.chateau.ui.conversations.create.namegroup;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;

import com.badoo.barf.mvp.BasePresenter;
import com.badoo.chateau.data.models.BaseConversation;
import com.badoo.chateau.core.model.User;
import com.badoo.chateau.core.usecases.conversations.CreateGroupConversation;

import java.util.List;

import rx.Subscription;

import static com.badoo.chateau.core.usecases.conversations.CreateGroupConversation.CreateGroupConversationParams;

public class NameGroupPresenterImpl extends BasePresenter<NameGroupView, NameGroupPresenter.NameGroupFlowListener> implements NameGroupPresenter {

    @NonNull
    private final CreateGroupConversation mGroupConversation;
    @NonNull
    private final List<String> mUserIds;

    public NameGroupPresenterImpl(@NonNull List<String> usersIds) {
        this(usersIds, new CreateGroupConversation());
    }

    @VisibleForTesting
    NameGroupPresenterImpl(@NonNull List<String> userIds,
                           @NonNull CreateGroupConversation groupConversation) {
        mGroupConversation = groupConversation;
        mUserIds = userIds;
    }

    @Override
    public void onCreateGroupClicked(@NonNull String name) {
        if (TextUtils.isEmpty(name)) {
            getView().showGroupNameEmptyError();
            return;
        }

        final CreateGroupConversationParams params = new CreateGroupConversationParams(mUserIds, name);
        final Subscription createGroupSub = mGroupConversation.execute(params)
            .map(conversation -> (BaseConversation) conversation)
            .subscribe(conversation -> {
                    getFlowListener().openChat(conversation.getId());
                },
                throwable -> {
                    throw new IllegalStateException("Can't create chat????", throwable);
                });
        trackSubscription(createGroupSub);
    }

}
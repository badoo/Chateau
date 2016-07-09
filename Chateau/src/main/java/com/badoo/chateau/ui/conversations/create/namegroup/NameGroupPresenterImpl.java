package com.badoo.chateau.ui.conversations.create.namegroup;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.badoo.barf.mvp.BaseRxPresenter;
import com.badoo.barf.rx.ScheduleOn;
import com.badoo.chateau.core.model.Conversation;
import com.badoo.chateau.core.usecases.conversations.CreateGroupConversation;

import java.util.List;

import rx.Subscription;

public class NameGroupPresenterImpl<C extends Conversation> extends BaseRxPresenter
    implements NameGroupPresenter {

    @NonNull
    private final CreateGroupConversation<C> mGroupConversation;
    @NonNull
    private final List<String> mUserIds;
    @NonNull
    private final NameGroupView mView;
    @NonNull
    private final NameGroupFlowListener<C> mFlowListener;

    public NameGroupPresenterImpl(@NonNull NameGroupView view,
                                  @NonNull NameGroupFlowListener<C> flowListener,
                                  @NonNull List<String> userIds,
                                  @NonNull CreateGroupConversation<C> groupConversation) {
        mView = view;
        mFlowListener = flowListener;
        mUserIds = userIds;
        mGroupConversation = groupConversation;
    }

    @Override
    public void onCreateGroupClicked(@NonNull String name) {
        if (TextUtils.isEmpty(name)) {
            mView.showGroupNameEmptyError();
            return;
        }

        final Subscription createGroupSub = mGroupConversation.execute(mUserIds, name)
            .compose(ScheduleOn.io())
            .subscribe(
                mFlowListener::requestOpenChat,
                throwable -> {
                    throw new IllegalStateException("Can't create chat????", throwable);
                });
        manage(createGroupSub);
    }

}
package com.badoo.chateau.ui.conversations.create.selectusers;

import android.support.annotation.NonNull;
import android.util.Log;

import com.badoo.barf.mvp.BaseRxPresenter;
import com.badoo.barf.rx.ScheduleOn;
import com.badoo.chateau.core.model.Conversation;
import com.badoo.chateau.core.usecases.conversations.CreateConversation;
import com.badoo.chateau.core.usecases.users.GetUsers;
import com.badoo.chateau.data.models.BaseUser;

import java.util.List;

import rx.Observable;
import rx.Subscription;

public class UserListPresenterImpl<U extends BaseUser, C extends Conversation> extends BaseRxPresenter
    implements UserListPresenter<U> {

    private static final String TAG = UserListPresenterImpl.class.getSimpleName();

    @NonNull
    private final UserListView<U> mView;
    private UserListFlowListener<C, U> mFlowListener;
    @NonNull
    private final GetUsers<U> mGetUsers;
    @NonNull
    private final CreateConversation<C> mCreateConversation;

    public UserListPresenterImpl(@NonNull UserListView<U> view,
                                 @NonNull UserListFlowListener<C, U> flowListener,
                                 @NonNull GetUsers<U> getUsers,
                                 @NonNull CreateConversation<C> createConversation) {
        mView = view;
        mFlowListener = flowListener;
        mGetUsers = getUsers;
        mCreateConversation = createConversation;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        trackSubscription(mGetUsers.execute()
            .compose(ScheduleOn.io())
            .flatMap(Observable::from)
            .toList()
            .subscribe(mView::showUsers, this::onFatalError));
    }


    @Override
    public void onUsersSelected(List<U> users) {
        if (users.isEmpty()) {
            return;
        }
        if (users.size() == 1) {
            final Subscription createConversationSub = mCreateConversation.execute(users.get(0).getUserId())
                .compose(ScheduleOn.io())
                .subscribe(
                    conversation -> {
                        mFlowListener.requestOpenChat(conversation);
                    },
                    this::onFatalError);
            trackSubscription(createConversationSub);
        }
        else {
            mFlowListener.requestCreateGroupChat(users);
        }
    }

    private void onFatalError(Throwable throwable) {
        Log.e(TAG, "Fatal error", throwable);
        mView.showError(true, throwable);
    }

}

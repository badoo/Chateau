package com.badoo.chateau.ui.conversations.create.selectusers;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.badoo.barf.mvp.BasePresenter;
import com.badoo.chateau.data.models.BaseConversation;
import com.badoo.chateau.data.models.BaseUser;
import com.badoo.chateau.core.usecases.conversations.CreateConversation;
import com.badoo.chateau.core.usecases.users.GetUsers;

import java.util.List;

import rx.Observable;
import rx.Subscription;

import static com.badoo.barf.usecase.UseCase.NoParams;

public class UserListPresenterImpl extends BasePresenter<UserListView, UserListPresenter.UserListFlowListener> implements UserListPresenter {

    private static final String TAG = UserListPresenterImpl.class.getSimpleName();

    @NonNull
    private final GetUsers mGetUsers;
    @NonNull
    private final CreateConversation mCreateConversation;

    public UserListPresenterImpl() {
        this(new GetUsers(), new CreateConversation());
    }

    @VisibleForTesting
    UserListPresenterImpl(@NonNull GetUsers getUsers, @NonNull CreateConversation createConversation) {
        mGetUsers = getUsers;
        mCreateConversation = createConversation;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        trackSubscription(mGetUsers.execute(NoParams.NONE)
            .flatMap(Observable::from)
            .map(user -> (BaseUser) user)
            .toList()
            .subscribe(getView()::showUsers, this::onError));
    }

    @Override
    public void onUsersSelected(List<BaseUser> users) {
        if (users.isEmpty()) {
            return;
        }
        if (users.size() == 1) {
            final Subscription createConversationSub = mCreateConversation.execute(users.get(0))
                .map(conversation -> (BaseConversation) conversation)
                .subscribe(
                    conversation -> {
                        getFlowListener().openChat(conversation.getId());
                    },
                    this::onError);
            trackSubscription(createConversationSub);
        }
        else {
            getFlowListener().createGroupChat(users);
        }
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Failed to load data", throwable);
        getView().showGenericError();
    }

}

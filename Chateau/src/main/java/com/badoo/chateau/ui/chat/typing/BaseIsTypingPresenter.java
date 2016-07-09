package com.badoo.chateau.ui.chat.typing;

import android.support.annotation.NonNull;

import com.badoo.barf.mvp.BaseRxPresenter;
import com.badoo.barf.rx.ScheduleOn;
import com.badoo.chateau.core.model.User;
import com.badoo.chateau.core.usecases.istyping.SendUserIsTyping;
import com.badoo.chateau.core.usecases.istyping.SubscribeToUsersTyping;

import rx.android.schedulers.AndroidSchedulers;

public class BaseIsTypingPresenter<U extends User> extends BaseRxPresenter implements IsTypingPresenter<U> {

    @NonNull
    private final IsTypingView<U> mView;
    @NonNull
    private final String mConversationId;
    @NonNull
    private final SubscribeToUsersTyping<U> mSubscribeToUsersTyping;
    @NonNull
    private final SendUserIsTyping mSendUserIsTyping;

    public BaseIsTypingPresenter(@NonNull IsTypingView<U> view,
                                 @NonNull String conversationId,
                                 @NonNull SubscribeToUsersTyping<U> subscribeToUsersTyping,
                                 @NonNull SendUserIsTyping sendUserIsTyping) {
        mView = view;
        mConversationId = conversationId;
        mSubscribeToUsersTyping = subscribeToUsersTyping;
        mSendUserIsTyping = sendUserIsTyping;
    }

    @Override
    public void onStart() {
        manage(mSubscribeToUsersTyping.execute(mConversationId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(mView::showOtherUserTyping));
    }

    @Override
    public void onUserTyping() {
        manage(mSendUserIsTyping.execute(mConversationId)
            .compose(ScheduleOn.io())
            .subscribe());
    }
}

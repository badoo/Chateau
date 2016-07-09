package com.badoo.chateau.ui.chat.info;

import android.support.annotation.NonNull;

import com.badoo.barf.mvp.BaseRxPresenter;
import com.badoo.barf.rx.ScheduleOn;
import com.badoo.chateau.core.model.Conversation;
import com.badoo.chateau.core.usecases.conversations.GetConversation;

/**
 */
public class BaseChatInfoPresenter<C extends Conversation> extends BaseRxPresenter implements ChatInfoPresenter<C> {

    @NonNull
    private final ChatInfoView<C> mView;
    @NonNull
    private final String mConversationId;
    @NonNull
    private final GetConversation<C> mGetConversation;

    public BaseChatInfoPresenter(@NonNull ChatInfoView<C> view,
                                 @NonNull String conversationId,
                                 @NonNull GetConversation<C> getConversation) {
        mView = view;
        mConversationId = conversationId;
        mGetConversation = getConversation;
    }

    @Override
    public void onStart() {
        super.onStart();
        manage(mGetConversation.execute(mConversationId)
            .compose(ScheduleOn.io())
            .subscribe(mView::showConversation));
    }
}

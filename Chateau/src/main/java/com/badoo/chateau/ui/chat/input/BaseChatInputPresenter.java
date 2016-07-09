package com.badoo.chateau.ui.chat.input;

import android.support.annotation.NonNull;

import com.badoo.barf.mvp.BaseRxPresenter;
import com.badoo.barf.rx.ScheduleOn;
import com.badoo.chateau.core.model.Message;
import com.badoo.chateau.core.usecases.messages.SendMessage;

public class BaseChatInputPresenter<M extends Message> extends BaseRxPresenter
    implements ChatInputPresenter<M> {

    @NonNull
    private final ChatInputView mView;
    @NonNull
    private final SendMessage<M> mSendMessage;
    private final String mChatId;

    public BaseChatInputPresenter(@NonNull String chatId,
                                  @NonNull ChatInputView view,
                                  @NonNull SendMessage<M> sendMessage) {
        mChatId = chatId;
        mView = view;
        mSendMessage = sendMessage;
    }

    public String getChatId() {
        return mChatId;
    }

    @Override
    public void onSendMessage(@NonNull M message) {
        manage(mSendMessage.execute(mChatId, message)
            .compose(ScheduleOn.io()).subscribe());
        mView.clearText();
    }

}

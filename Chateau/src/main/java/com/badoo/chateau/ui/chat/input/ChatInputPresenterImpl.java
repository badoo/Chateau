package com.badoo.chateau.ui.chat.input;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.badoo.barf.mvp.BaseRxPresenter;
import com.badoo.barf.rx.ScheduleOn;
import com.badoo.chateau.core.usecases.istyping.SendUserIsTyping;
import com.badoo.chateau.core.usecases.messages.SendMessage;

public class ChatInputPresenterImpl extends BaseRxPresenter
    implements ChatInputPresenter {

    @NonNull
    private final ChatInputView mView;
    @NonNull
    private final ChatInputFlowListener mFlowListener;
    @NonNull
    private final SendMessage mSendMessage;
    @NonNull
    private final SendUserIsTyping mSendUserIsTyping;
    private final String mChatId;

    public ChatInputPresenterImpl(@NonNull String chatId,
                                  @NonNull ChatInputView view,
                                  @NonNull ChatInputFlowListener flowListener,
                                  @NonNull SendMessage sendMessage,
                                  @NonNull SendUserIsTyping sendUserIsTyping) {
        mChatId = chatId;
        mView = view;
        mFlowListener = flowListener;
        mSendMessage = sendMessage;
        mSendUserIsTyping = sendUserIsTyping;
    }

    @Override
    public void onSendMessage(@NonNull String message) {
        if (!TextUtils.isEmpty(message)) {
            trackSubscription(mSendMessage.execute(mChatId, message, null)
                .compose(ScheduleOn.io()).subscribe());
        }
        mView.clearText();
    }

    @Override
    public void onPickImage() {
        mFlowListener.requestPickLocalImageForMessage();
    }

    @Override
    public void onTakePhoto() {
        mFlowListener.requestTakePhotoForMessage();
    }

    @Override
    public void onSendImage(@NonNull Uri uri) {
        trackSubscription(mSendMessage.execute(mChatId, null, uri)
            .compose(ScheduleOn.io()).subscribe());
    }

    @Override
    public void onUserTyping() {
        trackSubscription(mSendUserIsTyping.execute(mChatId)
            .compose(ScheduleOn.io()).subscribe());
    }

}

package com.badoo.chateau.ui.chat.input;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;

import com.badoo.barf.mvp.BasePresenter;
import com.badoo.chateau.data.models.BaseMessage;
import com.badoo.chateau.data.models.payloads.ImagePayload;
import com.badoo.chateau.data.models.payloads.TextPayload;
import com.badoo.chateau.core.model.Message;
import com.badoo.chateau.core.usecases.istyping.SendUserIsTyping;
import com.badoo.chateau.core.usecases.messages.ChatParams;
import com.badoo.chateau.core.usecases.messages.SendMessage;

import static com.badoo.chateau.core.usecases.messages.SendMessage.SendMessageParams;

public class ChatInputPresenterImpl extends BasePresenter<ChatInputPresenter.ChatInputView, ChatInputPresenter.ChatInputFlowListener> implements ChatInputPresenter {

    @NonNull
    private final SendMessage mSendMessage;
    @NonNull
    private final SendUserIsTyping mSendUserIsTyping;
    private final String mChatId;

    public ChatInputPresenterImpl(@NonNull String chatId) {
        this(chatId, new SendMessage(), new SendUserIsTyping());
    }

    @VisibleForTesting
    public ChatInputPresenterImpl(@NonNull String chatId,
                                  @NonNull SendMessage sendMessage, @NonNull SendUserIsTyping sendUserIsTyping) {
        mChatId = chatId;
        mSendMessage = sendMessage;
        mSendUserIsTyping = sendUserIsTyping;
    }

    @Override
    public void onSendMessage(@NonNull String message) {
        if (!TextUtils.isEmpty(message)) {
            final Message msg = BaseMessage.createOutgoingMessage(new TextPayload(message));
            trackSubscription(mSendMessage.execute(new SendMessageParams(mChatId, msg)).subscribe());
        }
        getView().clearText();
    }

    @Override
    public void onPickImage() {
        getFlowListener().requestPickLocalImageForMessage();
    }

    @Override
    public void onTakePhoto() {
        getFlowListener().requestTakePhotoForMessage();
    }

    @Override
    public void onSendImage(@NonNull Uri uri) {
        final Message msg = BaseMessage.createOutgoingMessage(new ImagePayload(uri.toString()));
        trackSubscription(mSendMessage.execute(new SendMessageParams(mChatId, msg)).subscribe());
    }

    @Override
    public void onUserTyping() {
        trackSubscription(mSendUserIsTyping.execute(new ChatParams(mChatId)).subscribe());
    }

}

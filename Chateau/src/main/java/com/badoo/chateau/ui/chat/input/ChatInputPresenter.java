package com.badoo.chateau.ui.chat.input;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.badoo.barf.mvp.Presenter;

public interface ChatInputPresenter extends Presenter<ChatInputView, ChatInputPresenter.ChatInputFlowListener> {

    void onSendMessage(@NonNull String message);

    void onPickImage();

    void onTakePhoto();

    void onSendImage(@NonNull Uri uri);

    void onUserTyping();

    interface ChatInputFlowListener extends Presenter.FlowListener {

        void pickLocalImageForMessage();

        void takePhotoForMessage();

    }
}

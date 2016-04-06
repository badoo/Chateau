package com.badoo.chateau.ui.chat.input;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.badoo.barf.mvp.Presenter;
import com.badoo.barf.mvp.View;

public interface ChatInputPresenter extends Presenter<ChatInputPresenter.ChatInputView, ChatInputPresenter.ChatInputFlowListener> {

    void onSendMessage(@NonNull String message);

    void onPickImage();

    void onTakePhoto();

    void onSendImage(@NonNull Uri uri);

    void onUserTyping();

    interface ChatInputView extends View<ChatInputPresenter> {

        void clearText();

    }

    interface ChatInputFlowListener extends Presenter.FlowListener {

        void requestPickLocalImageForMessage();

        void requestTakePhotoForMessage();

    }
}

package com.badoo.chateau.example.ui.chat.input;

import android.support.annotation.NonNull;

import com.badoo.chateau.core.usecases.messages.SendMessage;
import com.badoo.chateau.example.data.model.ExampleMessage;
import com.badoo.chateau.ui.chat.input.BaseChatInputPresenter;

public class ExampleChatInputPresenterImpl extends BaseChatInputPresenter<ExampleMessage> implements ExampleChatInputPresenter {

    public ExampleChatInputPresenterImpl(@NonNull String chatId, @NonNull ChatInputView view,
                                         @NonNull SendMessage<ExampleMessage> sendMessage) {
        super(chatId, view, sendMessage);
    }
}

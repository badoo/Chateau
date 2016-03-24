package com.badoo.chateau.core.usecases.messages;

import android.support.annotation.NonNull;

public class ChatParams {

    public final String mChatId;

    public ChatParams(@NonNull String chatId) {
        mChatId = chatId;
    }
}

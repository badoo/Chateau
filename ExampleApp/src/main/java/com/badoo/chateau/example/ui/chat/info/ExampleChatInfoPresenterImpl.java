package com.badoo.chateau.example.ui.chat.info;

import android.support.annotation.NonNull;

import com.badoo.chateau.core.usecases.conversations.GetConversation;
import com.badoo.chateau.example.data.model.ExampleConversation;
import com.badoo.chateau.ui.chat.info.BaseChatInfoPresenter;

public class ExampleChatInfoPresenterImpl extends BaseChatInfoPresenter<ExampleConversation> implements ExampleChatInfoPresenter {

    public ExampleChatInfoPresenterImpl(@NonNull ChatInfoView<ExampleConversation> view,
                                        @NonNull String conversationId,
                                        @NonNull GetConversation<ExampleConversation> getConversation) {
        super(view, conversationId, getConversation);
    }
}

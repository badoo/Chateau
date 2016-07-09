package com.badoo.chateau.example.ui.chat.info;

import com.badoo.chateau.example.data.model.ExampleConversation;
import com.badoo.chateau.ui.chat.info.ChatInfoPresenter;

public interface ExampleChatInfoPresenter extends ChatInfoPresenter<ExampleConversation> {

    interface ExampleChatInfoView extends ChatInfoView<ExampleConversation> {
    }
}

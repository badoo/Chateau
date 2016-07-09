package com.badoo.chateau.example.ui.chat.istyping;

import android.support.annotation.NonNull;

import com.badoo.chateau.core.usecases.istyping.SendUserIsTyping;
import com.badoo.chateau.core.usecases.istyping.SubscribeToUsersTyping;
import com.badoo.chateau.example.data.model.ExampleUser;
import com.badoo.chateau.ui.chat.typing.BaseIsTypingPresenter;

public class ExampleIsTypingPresenterImpl extends BaseIsTypingPresenter<ExampleUser> implements ExampleIsTypingPresenter {

    public ExampleIsTypingPresenterImpl(@NonNull IsTypingView<ExampleUser> view, @NonNull String conversationId,
                                        @NonNull SubscribeToUsersTyping<ExampleUser> subscribeToUsersTyping,
                                        @NonNull SendUserIsTyping sendUserIsTyping) {
        super(view, conversationId, subscribeToUsersTyping, sendUserIsTyping);
    }
}

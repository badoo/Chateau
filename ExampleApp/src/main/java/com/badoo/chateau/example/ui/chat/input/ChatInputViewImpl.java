package com.badoo.chateau.example.ui.chat.input;

import android.support.annotation.NonNull;

import com.badoo.barf.mvp.MvpView;
import com.badoo.barf.mvp.PresenterFactory;
import com.badoo.chateau.example.R;
import com.badoo.chateau.example.data.model.ExampleMessage;
import com.badoo.chateau.example.ui.widgets.ChatTextInputView;
import com.badoo.chateau.extras.ViewFinder;
import com.badoo.chateau.ui.chat.input.ChatInputPresenter;

import static com.badoo.chateau.ui.chat.input.ChatInputPresenter.ChatInputView;

public class ChatInputViewImpl implements ChatInputView {

    private final ChatTextInputView mInput;
    @NonNull
    private final ChatInputPresenter<ExampleMessage> mPresenter;

    public ChatInputViewImpl(@NonNull String conversationId,
                             @NonNull ViewFinder viewFinder,
                             @NonNull PresenterFactory<ChatInputView, ChatInputPresenter<ExampleMessage>> presenterFactory) {
        mPresenter = presenterFactory.init(this);
        mInput = viewFinder.findViewById(R.id.chat_input);
        mInput.setOnSendClickListener(v -> mPresenter.onSendMessage(ExampleMessage.createOutgoingTextMessage(conversationId, mInput.getText())));
    }

    @Override
    public void clearText() {
        mInput.clearText();
    }

}

package com.badoo.chateau.example.ui.chat.input;

import android.support.annotation.NonNull;

import com.badoo.barf.mvp.MvpView;
import com.badoo.barf.mvp.PresenterFactory;
import com.badoo.chateau.example.R;
import com.badoo.chateau.example.ui.widgets.ChatTextInputView;
import com.badoo.chateau.extras.ViewFinder;
import com.badoo.chateau.ui.chat.input.ChatInputPresenter;

import static com.badoo.chateau.ui.chat.input.ChatInputPresenter.*;

public class ChatInputViewImpl implements ChatInputView, MvpView {

    private final ChatTextInputView mInput;
    @NonNull
    private final ChatInputPresenter mPresenter;

    public ChatInputViewImpl(@NonNull ViewFinder viewFinder,
                             @NonNull PresenterFactory<ChatInputView, ChatInputPresenter> presenterFactory) {
        mPresenter = presenterFactory.init(this);
        mInput = viewFinder.findViewById(R.id.chat_input);
        mInput.setOnSendClickListener(v -> mPresenter.onSendMessage(mInput.getText()));
        mInput.setOnActionItemClickedListener(item -> {
            if (item.getItemId() == R.id.action_attachPhoto) {
                mPresenter.onPickImage();
            }
            else if (item.getItemId() == R.id.action_takePhoto) {
                mPresenter.onTakePhoto();
            }
            return true;
        });
        mInput.setOnTypingListener(mPresenter::onUserTyping);

    }

    @Override
    public void clearText() {
        mInput.clearText();
    }

}

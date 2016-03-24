package com.badoo.chateau.example.ui.chat.input;

import android.support.annotation.NonNull;

import com.badoo.barf.mvp.BaseView;
import com.badoo.chateau.example.R;
import com.badoo.chateau.example.ui.widgets.ChatTextInputView;
import com.badoo.chateau.ui.chat.input.ChatInputPresenter;
import com.badoo.chateau.ui.chat.input.ChatInputView;
import com.badoo.chateau.example.ui.util.ViewFinder;

public class ChatInputViewImpl extends BaseView<ChatInputPresenter> implements ChatInputView {

    private final ChatTextInputView mInput;

    public ChatInputViewImpl(@NonNull ViewFinder viewFinder) {
        mInput = viewFinder.findViewById(R.id.chat_input);
        mInput.setOnSendClickListener(v -> {
            getPresenter().onSendMessage(mInput.getText());
        });
        mInput.setOnActionItemClickedListener(item -> {
            if (item.getItemId() == R.id.action_attachPhoto) {
                getPresenter().onPickImage();
            }
            else if (item.getItemId() == R.id.action_takePhoto) {
                getPresenter().onTakePhoto();
            }
            return true;
        });
    }

    @Override
    protected void onPresenterAttached(@NonNull ChatInputPresenter presenter) {
        mInput.setOnTypingListener(presenter::onUserTyping);
    }

    @Override
    public void clearText() {
        mInput.clearText();
    }

}

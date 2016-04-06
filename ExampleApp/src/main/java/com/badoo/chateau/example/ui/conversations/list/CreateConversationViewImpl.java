package com.badoo.chateau.example.ui.conversations.list;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;

import com.badoo.barf.mvp.BaseView;
import com.badoo.chateau.example.R;
import com.badoo.chateau.example.ui.conversations.list.CreateConversationPresenter.CreateConversationView;
import com.badoo.chateau.extras.ViewFinder;

public class CreateConversationViewImpl extends BaseView<CreateConversationPresenter> implements CreateConversationView {

    public CreateConversationViewImpl(@NonNull ViewFinder viewFinder) {
        FloatingActionButton startNewChat = viewFinder.findViewById(R.id.conversations_start_new_chat_button);
        startNewChat.setOnClickListener(v -> getPresenter().onCreateNewConversationClicked());
    }
}

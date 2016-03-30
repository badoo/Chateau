package com.badoo.chateau.core.usecases.messages;

import android.support.annotation.VisibleForTesting;

import com.badoo.barf.data.repo.Repositories;
import com.badoo.chateau.core.model.Message;
import com.badoo.chateau.core.repos.messages.MessageQuery;
import com.badoo.chateau.core.repos.messages.MessageRepository;

import rx.Observable;

public class SubscribeToNewMessages extends BaseChatUseCase<ChatParams, Message> {

    public SubscribeToNewMessages() {
        super(Repositories.getRepo(MessageRepository.KEY));
    }

    @VisibleForTesting
    SubscribeToNewMessages(MessageRepository messageRepository) {
        super(messageRepository);
    }

    @Override
    protected Observable<Message> createObservable(ChatParams params) {
        return getRepo().query(new MessageQuery.SubscribeToNewMessagesForConversation(params.mChatId));
    }
}

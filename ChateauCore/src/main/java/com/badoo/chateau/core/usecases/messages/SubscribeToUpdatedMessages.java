package com.badoo.chateau.core.usecases.messages;

import android.support.annotation.VisibleForTesting;

import com.badoo.barf.data.repo.Repositories;
import com.badoo.chateau.core.model.Message;
import com.badoo.chateau.core.repos.messages.MessageQuery;
import com.badoo.chateau.core.repos.messages.MessageRepository;

import rx.Observable;

public class SubscribeToUpdatedMessages extends BaseChatUseCase<ChatParams, Message> {

    public SubscribeToUpdatedMessages() {
        super(Repositories.getRepo(MessageRepository.KEY));
    }

    @VisibleForTesting
    SubscribeToUpdatedMessages(MessageRepository messageRepository) {
        super(messageRepository);
    }

    @Override
    protected Observable<Message> createObservable(ChatParams params) {
        return getRepo().query(new MessageQuery.GetUpdatedMessagesForConversation(params.mChatId));
    }
}

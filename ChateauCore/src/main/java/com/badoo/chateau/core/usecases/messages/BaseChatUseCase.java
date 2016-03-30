package com.badoo.chateau.core.usecases.messages;

import com.badoo.barf.usecase.UseCase;
import com.badoo.chateau.core.repos.messages.MessageRepository;

public abstract class BaseChatUseCase<P, R> extends UseCase<P, R> {

    private final MessageRepository mMessageRepository;

    public BaseChatUseCase(MessageRepository messageRepository) {
        mMessageRepository = messageRepository;
    }

    protected MessageRepository getRepo() {
        return mMessageRepository;
    }
}

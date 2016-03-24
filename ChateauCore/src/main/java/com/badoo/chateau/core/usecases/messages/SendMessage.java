package com.badoo.chateau.core.usecases.messages;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.badoo.barf.data.repo.Repositories;
import com.badoo.chateau.core.model.Message;
import com.badoo.chateau.core.repos.messages.MessageQuery;
import com.badoo.chateau.core.repos.messages.MessageRepository;

import rx.Observable;

public class SendMessage extends BaseChatUseCase<SendMessage.SendMessageParams, Void> {

    public SendMessage() {
        super(Repositories.getRepo(MessageRepository.KEY));
    }

    @VisibleForTesting
    protected SendMessage(MessageRepository messageRepository) {
        super(messageRepository);
    }

    @Override
    protected Observable<Void> createObservable(SendMessageParams params) {
        getRepo().query(new MessageQuery.SendMessage(params.mChatId, params.mMessage));
        return Observable.empty();
    }

    public static class SendMessageParams extends ChatParams {
        private final Message mMessage;

        public SendMessageParams(@NonNull String chatId, Message message) {
            super(chatId);
            mMessage = message;
        }
    }
}

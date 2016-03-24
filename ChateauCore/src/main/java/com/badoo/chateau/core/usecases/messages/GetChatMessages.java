package com.badoo.chateau.core.usecases.messages;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.badoo.barf.data.repo.Repositories;
import com.badoo.chateau.core.model.Message;
import com.badoo.chateau.core.repos.messages.MessageQuery;
import com.badoo.chateau.core.repos.messages.MessageRepository;

import java.util.List;

import rx.Observable;

public class GetChatMessages extends BaseChatUseCase<GetChatMessages.GetChatMessagesParams, List<Message>> {

    public GetChatMessages() {
        super(Repositories.getRepo(MessageRepository.KEY));
    }

    @VisibleForTesting
    GetChatMessages(MessageRepository messageRepository) {
        super(messageRepository);
    }

    @Override
    protected Observable<List<Message>> createObservable(GetChatMessagesParams params) {
        return getRepo().query(new MessageQuery.GetMessages(params.mChatId, params.mChunkBefore)).toList();
    }

    public static class GetChatMessagesParams extends ChatParams {

        @Nullable
        private final Message mChunkBefore;

        public GetChatMessagesParams(@NonNull String chatId) {
            super(chatId);
            mChunkBefore = null;
        }

        public GetChatMessagesParams(@NonNull String chatId, @Nullable Message chunkBefore) {
            super(chatId);
            mChunkBefore = chunkBefore;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            GetChatMessagesParams that = (GetChatMessagesParams) o;

            return mChunkBefore != null ? mChunkBefore.equals(that.mChunkBefore) : that.mChunkBefore == null;

        }

        @Override
        public int hashCode() {
            return mChunkBefore != null ? mChunkBefore.hashCode() : 0;
        }
    }
}

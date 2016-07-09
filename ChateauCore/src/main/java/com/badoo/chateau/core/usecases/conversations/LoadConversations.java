package com.badoo.chateau.core.usecases.conversations;

import android.support.annotation.NonNull;

import com.badoo.barf.data.repo.Repository;
import com.badoo.barf.usecase.UseCase;
import com.badoo.chateau.core.model.Conversation;
import com.badoo.chateau.core.repos.conversations.ConversationDataSource.LoadResult;

import rx.Observable;

import static com.badoo.chateau.core.repos.conversations.ConversationQueries.LoadConversationsQuery;
import static com.badoo.chateau.core.repos.conversations.ConversationQueries.LoadConversationsQuery.*;

/**
 * Use case for retrieving all conversations that the current user is involved in.
 */
@UseCase
public class LoadConversations<C extends Conversation> {

    private Repository<C> mConversationRepository;

    public LoadConversations(Repository<C> conversationRepository) {
        mConversationRepository = conversationRepository;
    }

    public final Observable<LoadResult<C>> all() {
        return execute(Type.ALL);
    }

    public final Observable<LoadResult<C>> newer() {
        return execute(Type.NEWER);
    }

    public final Observable<LoadResult<C>> older() {
        return execute(Type.OLDER);
    }

    protected Observable<LoadResult<C>> execute(@NonNull Type type) {
        return mConversationRepository.query(new LoadConversationsQuery<>(type, null, null));
    }

}

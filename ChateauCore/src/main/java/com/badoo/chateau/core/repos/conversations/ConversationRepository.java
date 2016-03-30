package com.badoo.chateau.core.repos.conversations;

import com.badoo.barf.data.repo.DelegatingRepository;
import com.badoo.barf.data.repo.Repositories;
import com.badoo.chateau.core.model.Conversation;

public class ConversationRepository extends DelegatingRepository<ConversationQuery, Conversation> {

    public final static Repositories.Key<ConversationRepository> KEY = new Repositories.Key<>("ConversationsRepository");
}

package com.badoo.chateau.core.repos.messages;

import com.badoo.barf.data.repo.DelegatingRepository;
import com.badoo.barf.data.repo.Repositories;
import com.badoo.chateau.core.model.Message;

public class MessageRepository extends DelegatingRepository<MessageQuery, Message> {

    public final static Repositories.Key<MessageRepository> KEY = new Repositories.Key<>("MessageRepository");
}

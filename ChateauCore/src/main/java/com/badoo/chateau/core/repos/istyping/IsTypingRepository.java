package com.badoo.chateau.core.repos.istyping;

import com.badoo.barf.data.repo.DelegatingRepository;
import com.badoo.barf.data.repo.Repositories;
import com.badoo.chateau.core.model.User;

public class IsTypingRepository extends DelegatingRepository<IsTypingQuery, User> {

    public final static Repositories.Key<IsTypingRepository> KEY = new Repositories.Key<>("IsTypingRepository");
}

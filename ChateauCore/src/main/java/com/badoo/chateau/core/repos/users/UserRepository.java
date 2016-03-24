package com.badoo.chateau.core.repos.users;

import com.badoo.barf.data.repo.DelegatingRepository;
import com.badoo.barf.data.repo.Repositories;
import com.badoo.chateau.core.model.User;

public class UserRepository extends DelegatingRepository<UserQuery, User> {

    public final static Repositories.Key<UserRepository> KEY = new Repositories.Key<>("UserRepository");
}

package com.badoo.chateau.example.ui;

import com.badoo.barf.data.repo.Repository;
import com.badoo.chateau.example.data.model.ExampleConversation;
import com.badoo.chateau.example.data.model.ExampleMessage;
import com.badoo.chateau.example.data.model.ExampleUser;

/**
 * Injector that provides instances of the Repositories needed
 */
public abstract class ExampleConfiguration<T> implements Injector.Configuration<T> {

    public static Repository<ExampleMessage> sMessageRepository;
    private static Repository<ExampleConversation> sConversationRepository;
    private static Repository<ExampleUser> sUserRepository;
    private static Repository<ExampleUser> sIsTypingRepository;
    private static Repository<ExampleUser> sSessionRepository;

    public static void setMessageRepository(Repository<ExampleMessage> repo) {
        sMessageRepository = repo;
    }

    public static void setConversationRepository(Repository<ExampleConversation> repo) {
        sConversationRepository = repo;
    }

    public static void setUsersRepository(Repository<ExampleUser> repo) {
        sUserRepository = repo;
    }

    public static void setIsTypingRepository(Repository<ExampleUser> isTypingRepository) {
        sIsTypingRepository = isTypingRepository;
    }

    public static void setSessionRepository(Repository<ExampleUser> sessionRepository) {
        sSessionRepository = sessionRepository;
    }

    protected Repository<ExampleMessage> getMessageRepo() {
        return sMessageRepository;
    }

    protected Repository<ExampleConversation> getConversationRepo() {
        return sConversationRepository;
    }

    protected Repository<ExampleUser> getUserRepo() {
        return sUserRepository;
    }

    public Repository<ExampleUser> getSessionRepo() {
        return sSessionRepository;
    }

    public Repository<ExampleUser> getIsTypingRepo() {
        return sIsTypingRepository;
    }
}

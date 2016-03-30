package com.badoo.barf.data.repo;

import android.support.annotation.NonNull;

/**
 * Class acting as a "repository of repositories" providing an instance of a certain repository type.
 * Repository instances needs to be registered with this class from your applications onCreate() method.
 * <p>
 * E.g:
 * <p>
 * Repositories.registerRepo(SessionRepository.KEY, new SessionRepository(new ParseSessionDataSource(...)));
 */
public final class Repositories {

    private Repositories() {
    }

    /**
     * Retrieves a Repository instance for a certain Key. The repository instance must previously have been registered.
     */
    public static <RepoType> RepoType getRepo(Key<RepoType> key) {
        RepoType instance = key.getInstance();
        if (instance == null) {
            throw new IllegalStateException("No repo registered for type: " + key.getName());
        }
        return instance;
    }

    /**
     * Registers a repository instance and associates it with a certain Key.
     */
    public static <RepoType> void registerRepo(@NonNull Key<RepoType> key, @NonNull RepoType instance) {
        if (key.getInstance() != null) {
            throw new IllegalStateException(String.format("Repository %s already registered for key", key.getInstance()));
        }
        key.setInstance(instance);
    }

    public static class Key<RepoType> {

        private final String mName;
        private RepoType mInstance;

        public Key(@NonNull String name) {
            mName = name;
        }

        private RepoType getInstance() {
            return mInstance;
        }

        private void setInstance(RepoType mInstance) {
            this.mInstance = mInstance;
        }

        private String getName() {
            return mName;
        }
    }
}

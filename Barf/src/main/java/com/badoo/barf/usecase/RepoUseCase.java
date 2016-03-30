package com.badoo.barf.usecase;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.badoo.barf.data.repo.Repositories;

public abstract class RepoUseCase<Q, R, Repo> extends UseCase<Q, R> {

    private final Repo mRepo;

    public RepoUseCase(Repositories.Key<Repo> key) {
        this(Repositories.getRepo(key));
    }

    @VisibleForTesting
    protected RepoUseCase(@NonNull Repo repository) {
        mRepo = repository;
    }

    protected Repo getRepo() {
        return mRepo;
    }

}

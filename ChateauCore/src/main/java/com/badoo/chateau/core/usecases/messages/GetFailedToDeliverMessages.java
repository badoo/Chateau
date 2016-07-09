package com.badoo.chateau.core.usecases.messages;

import com.badoo.barf.data.repo.Repository;
import com.badoo.barf.usecase.UseCase;
import com.badoo.chateau.core.model.Message;
import com.badoo.chateau.core.repos.messages.MessageQueries.GetUndeliveredQuery;

import java.util.List;

import rx.Observable;

@UseCase
public class GetFailedToDeliverMessages<M extends Message> {

    private final Repository<M> mRepo;

    public GetFailedToDeliverMessages(Repository<M> repo) {
        mRepo = repo;
    }

    public Observable<List<M>> execute() {
        return mRepo.query(new GetUndeliveredQuery<>());
    }

}

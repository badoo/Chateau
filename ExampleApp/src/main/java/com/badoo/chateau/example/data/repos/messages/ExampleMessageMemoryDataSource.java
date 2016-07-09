package com.badoo.chateau.example.data.repos.messages;

import android.support.annotation.NonNull;
import android.util.Log;

import com.badoo.chateau.core.repos.messages.MessageDataSource;
import com.badoo.chateau.core.repos.messages.MessageQueries;
import com.badoo.chateau.core.repos.messages.MessageQueries.LoadQuery;
import com.badoo.chateau.core.repos.messages.MessageQueries.SendQuery;
import com.badoo.chateau.core.repos.messages.MessageQueries.SubscribeQuery;
import com.badoo.chateau.example.data.model.ExampleMessage;
import com.badoo.chateau.example.data.repos.messages.cache.CacheEntry;
import com.badoo.chateau.example.data.repos.messages.cache.MessageMemoryCache;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

/**
 * Memory cached message data source built on top of ParseMessageDataSource.
 */
public class    ExampleMessageMemoryDataSource implements MessageDataSource<ExampleMessage> {

    private static final boolean DEBUG = true;
    private static final String TAG = ExampleMessageMemoryDataSource.class.getSimpleName();

    private final MessageDataSource<ExampleMessage> mNext;
    private final MessageMemoryCache mCache = new MessageMemoryCache();
    private final PublishSubject<Update<ExampleMessage>> mUpdatePublisher = PublishSubject.create();

    public ExampleMessageMemoryDataSource(ParseMessageDataSource next) {
        mNext = next;
        mNext.subscribe(new SubscribeQuery<>())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(update -> {
                final String conversationId = update.getConversationId();
                if (DEBUG) {
                    Log.d(TAG, "Updating cache for " + conversationId + " with data: " + update);
                }
                switch (update.getAction()) {
                    case ADDED:
                    case UPDATED:
                        if (mCache.hasData(conversationId)) {
                            mCache.get(conversationId).update(update);
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Action not supported: " + update.getAction());

                }
                mUpdatePublisher.onNext(update);
            });
    }

    @NonNull
    @Override
    public Observable<LoadResult<ExampleMessage>> load(@NonNull LoadQuery<ExampleMessage> query) {
        return loadFromCache(query).switchIfEmpty(loadFromNext(query));
    }

    private Observable<LoadResult<ExampleMessage>> loadFromCache(LoadQuery<ExampleMessage> query) {
        if (query.getType() == LoadQuery.Type.ALL && mCache.hasData(query.getConversationId())) {
            CacheEntry entry = mCache.get(query.getConversationId());
            if (DEBUG) {
                Log.d(TAG, "Cache contains " + entry.getMessages().size() + " message for query: " + query);
            }
            return Observable.just(new LoadResult<>(entry.getMessages(), entry.canLoadOlder(), entry.canLoadNewer()));
        }
        else {
            if (DEBUG) {
                Log.d(TAG, "No cached data for query: " + query);
            }
            return Observable.empty();
        }
    }

    private Observable<LoadResult<ExampleMessage>> loadFromNext(LoadQuery<ExampleMessage> query) {
        ExampleMessage oldest = null;
        ExampleMessage newest = null;
        if (mCache.hasData(query.getConversationId())) {
            final CacheEntry entry = mCache.get(query.getConversationId());
            oldest = entry.oldest();
            newest = entry.newest();
        }
        LoadQuery<ExampleMessage> updatedQuery = new LoadQuery<>(query.getConversationId(), query.getType(), oldest, newest);
        return mNext.load(updatedQuery)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(result -> mCache.get(query.getConversationId()).update(updatedQuery, result));
    }

    @NonNull
    @Override
    public Observable<List<ExampleMessage>> getUndelivered(@NonNull MessageQueries.GetUndeliveredQuery<ExampleMessage> query) {
        return mNext.getUndelivered(query);
    }

    @NonNull
    @Override
    public Observable<Void> send(@NonNull SendQuery<ExampleMessage> query) {
        return mNext.send(query);
    }

    @NonNull
    @Override
    public Observable<Update<ExampleMessage>> subscribe(@NonNull SubscribeQuery<ExampleMessage> query) {
        final String conversationId = query.getConversationId();
        return mUpdatePublisher.filter(update -> update.getConversationId().equals(conversationId));
    }

}

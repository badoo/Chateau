package com.badoo.chateau.example.data.repos.messages;

import android.support.annotation.NonNull;
import android.util.Log;

import com.badoo.chateau.core.repos.messages.MessageDataSource;
import com.badoo.chateau.core.repos.messages.MessageQueries.LoadMessagesQuery;
import com.badoo.chateau.core.repos.messages.MessageQueries.SendMessageQuery;
import com.badoo.chateau.core.repos.messages.MessageQueries.SubscribeToMessagesQuery;
import com.badoo.chateau.example.data.model.ExampleMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscription;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;

/**
 * Memory cached message data source built on top of ParseMessageDataSource.
 */
public class ExampleMessageMemoryDataSource implements MessageDataSource<ExampleMessage> {

    private static final boolean DEBUG = true;
    private static final String TAG = ExampleMessageMemoryDataSource.class.getSimpleName();

    private final ParseMessageDataSource mNetworkDataSource;
    private final MessageMemoryCache mMessageMemoryCache = new MessageMemoryCache();
    private Map<String, ReplaySubject<List<ExampleMessage>>> mPublishers = new HashMap<>();
    private Map<String, Subscription> mNetworkSubscriptions = new HashMap<>();

    public ExampleMessageMemoryDataSource(ParseMessageDataSource networkDataSource) {
        mNetworkDataSource = networkDataSource;
    }

    @NonNull
    @Override
    public Observable<Boolean> loadMessages(@NonNull LoadMessagesQuery<ExampleMessage> query) {
        final String conversationId = query.getConversationId();
        if (query.getChunkBefore() == null && mMessageMemoryCache.hasDataForConversation(conversationId)) {
            if (DEBUG) {
                Log.d(TAG, "Data for conversation: " + query.getConversationId() + " available in memory cache");
            }
            // No "chunk before" is specified and we already got the data needed
            publishMessages(conversationId);
            return Observable.just(mMessageMemoryCache.getCachedDataForConversation(conversationId).isOldestLoaded());
        }
        else {
            if (DEBUG) {
                Log.d(TAG, "Data for conversation: " + query.getConversationId() + " not available in memory cache. Requesting from network");
            }
            // If a "chunk before" is specified or the cache is empty it means that we are trying to load additional data that is
            // not available in memory in this case we must simply delegate to the layer below
            if (!mNetworkSubscriptions.containsKey(conversationId)) {
                Observable<List<ExampleMessage>> network = mNetworkDataSource.subscribeToMessages(new SubscribeToMessagesQuery<>(conversationId));
                Subscription networkSubscription = network.subscribe(messages -> {
                    updateCacheAndPublish(conversationId, messages);
                });
                mNetworkSubscriptions.put(conversationId, networkSubscription);
            }
            return mNetworkDataSource.loadMessages(query);
        }
    }

    @Override
    public Observable<Void> sendMessage(@NonNull SendMessageQuery query) {
        return mNetworkDataSource.sendMessage(query);
    }

    @NonNull
    @Override
    public Observable<List<ExampleMessage>> subscribeToMessages(@NonNull SubscribeToMessagesQuery<ExampleMessage> query) {
        final String conversationId = query.getConversationId();
        return getPublisher(conversationId);
    }

    private void updateCacheAndPublish(@NonNull String conversationId, List<ExampleMessage> messages) {
        mMessageMemoryCache.update(conversationId, messages);
        publishMessages(conversationId);
    }

    private void publishMessages(@NonNull String conversationId) {
        CacheEntry entry = mMessageMemoryCache.getCachedDataForConversation(conversationId);
        final ReplaySubject<List<ExampleMessage>> publisher = getPublisher(conversationId);
        if (DEBUG && !publisher.hasObservers()) {
           Log.w(TAG, "Message publisher has no subscriptions! Completed: " + publisher.hasCompleted());
        }
        publisher.onNext(entry.getMessages());
    }

    private ReplaySubject<List<ExampleMessage>> getPublisher(@NonNull String conversationId) {
        if (!mPublishers.containsKey(conversationId)) {
            ReplaySubject<List<ExampleMessage>> publisher = ReplaySubject.createWithSize(1);
            mPublishers.put(conversationId, publisher);
        }
        return mPublishers.get(conversationId);
    }

}

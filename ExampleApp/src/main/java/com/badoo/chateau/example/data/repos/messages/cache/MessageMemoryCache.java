package com.badoo.chateau.example.data.repos.messages.cache;

import android.support.annotation.NonNull;

import com.badoo.chateau.example.data.model.ExampleMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Memory cache for messages
 */
public class MessageMemoryCache {

    private Map<String, CacheEntry> mCache = new HashMap<>();

    public synchronized boolean hasData(@NonNull String conversationId) {
        return mCache.containsKey(conversationId);
    }

    @NonNull
    public synchronized CacheEntry get(@NonNull String conversationId) {
        if (!mCache.containsKey(conversationId)) {
            mCache.put(conversationId, new CacheEntry());
        }
        return mCache.get(conversationId);
    }

}

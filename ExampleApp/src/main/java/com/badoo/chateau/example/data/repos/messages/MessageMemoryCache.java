package com.badoo.chateau.example.data.repos.messages;

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

    public synchronized boolean hasDataForConversation(@NonNull String conversationId) {
        return mCache.containsKey(conversationId);
    }

    public synchronized CacheEntry getCachedDataForConversation(@NonNull String conversationId) {
        return mCache.get(conversationId);
    }

    /**
     * Update the cache entry for a conversation
     */
    public synchronized void update(@NonNull String conversationId, @NonNull List<ExampleMessage> messages) {
        if (hasDataForConversation(conversationId)) {
            CacheEntry entry = getCachedDataForConversation(conversationId);
            entry.update(messages);
        }
        else {
            mCache.put(conversationId, new CacheEntry(messages));
        }
    }

}

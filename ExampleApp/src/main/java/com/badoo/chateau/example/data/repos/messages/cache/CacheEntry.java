package com.badoo.chateau.example.data.repos.messages.cache;

import android.support.annotation.NonNull;

import com.badoo.chateau.core.repos.messages.MessageDataSource.LoadResult;
import com.badoo.chateau.core.repos.messages.MessageDataSource.Update;
import com.badoo.chateau.core.repos.messages.MessageQueries.LoadQuery;
import com.badoo.chateau.example.data.model.ExampleMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Entry in the messages memory cache representing one
 */
public class CacheEntry {

    private static final boolean DEBUG = true;
    private static final String TAG = CacheEntry.class.getSimpleName();

    private final List<ExampleMessage> mMessages = new ArrayList<>();
    private boolean mCanLoadOlder;
    private boolean mCanLoadNewer;

    CacheEntry() {
    }

    @NonNull
    public List<ExampleMessage> getMessages() {
        return mMessages;
    }

    public void update(@NonNull Update<ExampleMessage> update) {
        switch (update.getAction()) {
            case ADDED:
                mMessages.add(update.getNewMessage());
                break;
            case UPDATED:
                replace(update.getNewMessage(), update.getOldMessage());
        }
    }

    public void update(@NonNull LoadQuery<ExampleMessage> query, @NonNull LoadResult<ExampleMessage> result) {
        switch (query.getType()) {
            case ALL:
                mMessages.clear();
                mMessages.addAll(result.getMessages());
                mCanLoadOlder = result.canLoadOlder();
                mCanLoadNewer = result.canLoadNewer();
                break;
            case OLDER:
                mMessages.addAll(0, result.getMessages());
                mCanLoadOlder = result.canLoadOlder();
                break;
            case NEWER:
                mMessages.addAll(result.getMessages());
                mCanLoadNewer = result.canLoadNewer();
                break;
        }
    }

    public boolean canLoadOlder() {
        return mCanLoadOlder;
    }

    public boolean canLoadNewer() {
        return mCanLoadNewer;
    }

    public ExampleMessage oldest() {
        return mMessages.isEmpty()? null : mMessages.get(0);
    }

    public ExampleMessage newest() {
        return mMessages.isEmpty()? null : mMessages.get(mMessages.size() - 1);
    }

    private void replace(ExampleMessage newMessage, ExampleMessage oldMessage) {
        if (oldMessage == null) {
            oldMessage = newMessage;
        }
        for (int i = mMessages.size() - 1; i >= 0; i--) {
            ExampleMessage candidate = mMessages.get(i);
            final boolean idMatches = !candidate.isUnconfirmed() && candidate.getId().equals(oldMessage.getId());
            final boolean localIdMatches = candidate.isUnconfirmed() && candidate.getLocalId().equals(oldMessage.getLocalId());
            if (idMatches || localIdMatches) {
                mMessages.set(i, newMessage);
                return;
            }
        }
    }

}

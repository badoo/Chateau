package com.badoo.chateau.example.data.repos.messages;

import android.support.annotation.NonNull;
import android.util.Log;

import com.badoo.chateau.example.data.model.ExampleMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Entry in the messages memory cache representing one
 */
class CacheEntry {

    private static final boolean DEBUG = true;
    private static final String TAG = CacheEntry.class.getSimpleName();

    private final List<ExampleMessage> mMessages;
    private boolean mOldestLoaded;

    CacheEntry(@NonNull List<ExampleMessage> messages) {
        mMessages = new ArrayList<>(messages);
        mOldestLoaded = false;
    }

    @NonNull
    public List<ExampleMessage> getMessages() {
        return mMessages;
    }

    public boolean isOldestLoaded() {
        return mOldestLoaded;
    }

    public synchronized void update(@NonNull List<ExampleMessage> messages) {
        if (!messages.isEmpty() && !messages.isEmpty()) {
            // Merge data
            merge(messages);
        }
        else {
            // Either no more was loaded or we loaded the first messages in a previously empty conversation
            mMessages.addAll(messages);
            mOldestLoaded = true; // In both cases we've loaded all there is
        }
    }

    private void merge(List<ExampleMessage> newMessages) {
        final int oldCount = mMessages.size();
        if (DEBUG) {
            Log.d(TAG, "Old messages");
            for (ExampleMessage msg : mMessages) {
                Log.d(TAG, msg.toString());
            }
            Log.d(TAG, "New messages");
            for (ExampleMessage msg : newMessages) {
                Log.d(TAG, msg.toString());
            }
        }
        final Map<String, ExampleMessage> localMessages = new HashMap<>();
        final Map<String, ExampleMessage> confirmedMessages = new HashMap<>();
        for (ExampleMessage msg : mMessages) {
            if (msg.isUnconfirmed()) {
                localMessages.put(msg.getLocalId(), msg);
            }
            else {
                confirmedMessages.put(msg.getId(), msg);
            }
        }
        // First check if we need to replace any messages based on either the id or local id
        final List<ExampleMessage> remainingMessages = new ArrayList<>();
        for (ExampleMessage msg : newMessages) {
            // Confirmed message (or updated local message) replacing a local message
            if (localMessages.containsKey(msg.getLocalId())) {
                replaceMessageInCache(localMessages.get(msg.getLocalId()), msg);
            }
            // Confirmed message replacing another confirmed message
            else if (confirmedMessages.containsKey(msg.getId())) {
                replaceMessageInCache(confirmedMessages.get(msg.getId()), msg);
            }
            else {
                remainingMessages.add(msg);
            }
        }
        newMessages = remainingMessages;
        if (!newMessages.isEmpty()) {
            // The remaining messages are new and should be inserted either at the start or beginning of the list
            if (newMessages.get(0).getTimestamp() >= newMessages.get(newMessages.size() - 1).getTimestamp()) {
                mMessages.addAll(newMessages);
            }
            else {
                // If they are not newer then we assume it's a page of older messages
                mMessages.addAll(0, newMessages);
            }
        }
        if (DEBUG) {
            Log.d(TAG, "Merged cache, diff: " + (mMessages.size() - oldCount));
        }
    }

    private void replaceMessageInCache(ExampleMessage oldMessage, ExampleMessage newMessage) {
        if (DEBUG) {
            Log.d(TAG, "Replacing cached message: " + oldMessage + ", with: " + newMessage);
        }
        final int index = mMessages.indexOf(oldMessage);
        mMessages.remove(index);
        if (mMessages.isEmpty()) {
            mMessages.add(newMessage);
        }
        else {
            mMessages.add(index, newMessage);
        }
    }

}

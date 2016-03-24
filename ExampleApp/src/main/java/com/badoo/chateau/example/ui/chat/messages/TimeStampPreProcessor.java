package com.badoo.chateau.example.ui.chat.messages;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.badoo.chateau.data.models.BaseMessage;
import com.badoo.chateau.data.models.payloads.TimestampPayload;

import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

public class TimeStampPreProcessor implements MessageListAdapter.ItemPreProcessor {
    final Calendar mCalendar;

    public TimeStampPreProcessor() {
        this(Calendar.getInstance());
    }

    TimeStampPreProcessor(Calendar calendar) {
        mCalendar = calendar;
    }


    public int doProcess(@NonNull List<BaseMessage> newMessages, @NonNull List<BaseMessage> currentMessages, int insertionPoint, @NonNull RecyclerView.AdapterDataObserver dataObserver) {
        if (newMessages.isEmpty()) {
            return insertionPoint;
        }

        final BaseMessage latestTimestamp = decorateWithTimestamps(newMessages);
        final BaseMessage oldestTimestamp = newMessages.get(0);
        if (shouldRemoveOldestTimestamp(currentMessages, insertionPoint - 1, oldestTimestamp)) {
            newMessages.remove(oldestTimestamp);
        }

        final int removeLocation;
        if ((removeLocation = shouldRemoveLatestTimestamp(currentMessages, insertionPoint, latestTimestamp)) != -1) {
            currentMessages.remove(removeLocation);
            dataObserver.onItemRangeRemoved(removeLocation, 1);
        }

        return insertionPoint;
    }

    private BaseMessage decorateWithTimestamps(List<BaseMessage> newMessages) {
        BaseMessage latestTimestamp = null;
        for (ListIterator<BaseMessage> iter = newMessages.listIterator(); iter.hasNext(); ) {
            final BaseMessage message = iter.next();
            if (latestTimestamp == null || !belongToCurrentDay(latestTimestamp.getTimestamp(), message.getTimestamp())) {
                iter.previous();
                latestTimestamp = BaseMessage.createTimestamp(startOfDay(message.getTimestamp()));
                iter.add(latestTimestamp);

            }
        }
        return latestTimestamp;
    }

    /**
     * Check if the oldest timestamp is already present in the current messages, return <code>true</code> if it should be removed before
     * the new messages are inserted.
     */
    private boolean shouldRemoveOldestTimestamp(@NonNull List<BaseMessage> currentMessages, int searchFrom, @NonNull BaseMessage oldestTimestamp) {
        if (currentMessages.isEmpty()) {
            return false;
        }

        for (int i = searchFrom; i >= 0; i--) {
            final BaseMessage message = currentMessages.get(i);
            if (message.getPayload() instanceof TimestampPayload) {
                if (message.getTimestamp() == oldestTimestamp.getTimestamp()) {
                    return true;
                }
                break;
            }
        }
        return false;
    }


    /**
     * Check if the latest timestamp exists in the current messages and if it does the index it occurs at or <code>-1</code> if it doesn't
     * occur.
     */
    private int shouldRemoveLatestTimestamp(List<BaseMessage> currentMessages, int searchFrom, BaseMessage latestTimestamp) {
        for (int i = searchFrom; i < currentMessages.size(); i++) {
            final BaseMessage message = currentMessages.get(i);
            if (message.getPayload() instanceof TimestampPayload) {
                if (message.getTimestamp() == latestTimestamp.getTimestamp()) {
                    return i;
                }
                break;
            }
        }
        return -1;
    }

    private boolean belongToCurrentDay(long startOfDay, long timestamp) {
        return timestamp < startOfDay + TimeUnit.DAYS.toMillis(1);
    }


    private long startOfDay(long timestamp) {
        mCalendar.setTimeInMillis(timestamp);
        mCalendar.set(Calendar.HOUR_OF_DAY, 0);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 0);

        return mCalendar.getTimeInMillis();
    }
}

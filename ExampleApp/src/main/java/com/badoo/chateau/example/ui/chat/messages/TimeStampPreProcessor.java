package com.badoo.chateau.example.ui.chat.messages;

import android.support.annotation.NonNull;

import com.badoo.chateau.example.data.model.ExampleMessage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

public class TimeStampPreProcessor implements ItemPreProcessor {
    final Calendar mCalendar;

    public TimeStampPreProcessor() {
        this(Calendar.getInstance());
    }

    TimeStampPreProcessor(Calendar calendar) {
        mCalendar = calendar;
    }

    @Override
    public List<ExampleMessage> doProcess(@NonNull List<ExampleMessage> input) {
        List<ExampleMessage> output = new ArrayList<>();
        ExampleMessage latestTimestamp = null;
        for (final ExampleMessage message : input) {
            if (latestTimestamp == null || !belongToCurrentDay(latestTimestamp.getTimestamp(), message.getTimestamp())) {
                latestTimestamp = ExampleMessage.createTimestamp(startOfDay(message.getTimestamp()));
                output.add(latestTimestamp);
            }
            output.add(message);
        }
        return output;
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

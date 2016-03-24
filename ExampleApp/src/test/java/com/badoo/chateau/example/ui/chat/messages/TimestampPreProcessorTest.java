package com.badoo.chateau.example.ui.chat.messages;

import android.support.v7.widget.RecyclerView;

import com.badoo.chateau.data.models.BaseMessage;
import com.badoo.chateau.data.models.payloads.TextPayload;
import com.badoo.chateau.data.models.payloads.TimestampPayload;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

public class TimestampPreProcessorTest {

    private Calendar mCalender = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    private TimeStampPreProcessor mTarget;
    private RecyclerView.AdapterDataObserver mMockDataObserver;

    @Before
    public void beforeTest() throws Exception {
        mCalender.setTimeInMillis(0);
        mTarget = new TimeStampPreProcessor(mCalender);
        mMockDataObserver = mock(RecyclerView.AdapterDataObserver.class);
    }


    @Test
    public void whenEmptyList_thenSingleTimestampIsAdded() {
        // Setup
        final List<BaseMessage> currentList = new ArrayList<>();
        final BaseMessage messageToInsert = createMessage(TimeUnit.HOURS.toMillis(6));
        final List<BaseMessage> newMessages = listOf(messageToInsert);

        // Execute
        mTarget.doProcess(newMessages, currentList, 0, mMockDataObserver);

        // Assert
        assertThat(newMessages.size(), is(2));
        assertThat(newMessages.get(0).getTimestamp(), is(0L));
        assertThat(newMessages.get(0).getPayload(), is(instanceOf(TimestampPayload.class)));
        assertThat(newMessages.get(1), is(messageToInsert));
        verifyZeroInteractions(mMockDataObserver);
    }

    @Test
    public void whenMessageIsInsertedForDayThatHasTimestamp_thenANewTimestampIsNotAdded() {
        // Setup
        final List<BaseMessage> currentList = new ArrayList<>();
        currentList.add(BaseMessage.createTimestamp(0));
        final BaseMessage messageToInsert = createMessage(TimeUnit.HOURS.toMillis(6));
        final List<BaseMessage> newMessages = listOf(messageToInsert);

        // Execute
        mTarget.doProcess(newMessages, currentList, 1, mMockDataObserver);

        // Assert
        assertThat(newMessages.size(), is(1));
        assertThat(newMessages.get(0), is(messageToInsert));
        verifyZeroInteractions(mMockDataObserver);
    }

    @Test
    public void whenMessageIsInsertedOnPreviousDayWithoutTimestamp_thenANewTimestampIsAdded() {
        // Setup
        final List<BaseMessage> currentList = new ArrayList<>();
        currentList.add(BaseMessage.createTimestamp(TimeUnit.HOURS.toMillis(24)));
        currentList.add(createMessage(TimeUnit.HOURS.toMillis(25)));

        final BaseMessage messageToInsert = createMessage(TimeUnit.HOURS.toMillis(6));
        final List<BaseMessage> newMessages = listOf(messageToInsert);

        mCalender.setTime(new Date(TimeUnit.HOURS.toMillis(25))); // 1h into second day

        // Execute
        mTarget.doProcess(newMessages, currentList, 0, mMockDataObserver);

        // Assert
        assertThat(newMessages.size(), is(2));
        assertThat(newMessages.get(0).getTimestamp(), is(0L));
        assertThat(newMessages.get(0).getPayload(), is(instanceOf(TimestampPayload.class)));
        assertThat(newMessages.get(1), is(messageToInsert));
    }


    @Test
    public void whenMessageIsInsertedOnNextDayWithoutTimestamp_thenANewTimestampIsAdded() {
        // Setup
        final List<BaseMessage> currentList = new ArrayList<>();
        currentList.add(BaseMessage.createTimestamp(TimeUnit.HOURS.toMillis(0)));
        currentList.add(createMessage(TimeUnit.HOURS.toMillis(6)));

        final BaseMessage messageToInsert = createMessage(TimeUnit.HOURS.toMillis(36));
        final List<BaseMessage> newMessages = listOf(messageToInsert);

        // Execute
        mTarget.doProcess(newMessages, currentList, 2, mMockDataObserver);

        // Assert
        assertThat(newMessages.size(), is(2));
        assertThat(newMessages.get(0).getTimestamp(), is(TimeUnit.HOURS.toMillis(24)));
        assertThat(newMessages.get(0).getPayload(), is(instanceOf(TimestampPayload.class)));
        assertThat(newMessages.get(1), is(messageToInsert));
    }


    private BaseMessage createMessage(long timestamp) {
        return new BaseMessage("", "", false, "", new TextPayload("Message"), timestamp, false);
    }

    private List<BaseMessage> listOf(BaseMessage msg, BaseMessage... msgs) {
        List<BaseMessage> messages = new ArrayList<>(1 + msgs.length);
        for (int i = msgs.length - 1; i >= 0; i--) {
            messages.add(msgs[i]);
        }

        if (messages.isEmpty()) {
            messages.add(msg);
        }
        else {
            messages.add(0, msg);
        }
        return messages;
    }

}
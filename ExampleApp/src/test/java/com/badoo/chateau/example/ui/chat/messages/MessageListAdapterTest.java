package com.badoo.chateau.example.ui.chat.messages;

import android.support.v7.widget.RecyclerView;

import com.badoo.chateau.data.models.BaseMessage;
import com.badoo.chateau.data.models.payloads.TextPayload;
import com.badoo.chateau.example.ui.utils.TestUtils;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MessageListAdapterTest {

    private MessageListAdapter mAdapter;
    private RecyclerView.AdapterDataObserver mDataObserver;

    @Before
    public void setupAdapter() throws Exception {
        mAdapter = new MessageListAdapter();
       mDataObserver = TestUtils.fixAdapterForTesting(mAdapter);
    }

    @Test
    public void replaceUnconfirmedWithFailedMessage() {
        // Given
        BaseMessage unconfirmed = BaseMessage.createUnconfirmedMessage("id", "from", new TextPayload("msg"), 0);
        mAdapter.addMessage(unconfirmed);

        // When
        BaseMessage failedMessage = BaseMessage.createFailedMessage(unconfirmed);
        mAdapter.addMessage(failedMessage);

        // Then
        assertEquals(1, mAdapter.getItemCount());
        assertSame(failedMessage, mAdapter.getMessage(0));
        verify(mDataObserver).onItemRangeChanged(0, 1, null);
    }

    @Test
    public void replaceUnconfirmedWithConfirmedMessage() {
        // Given
        BaseMessage unconfirmed = BaseMessage.createUnconfirmedMessage("id", "from", new TextPayload("msg"), 0);
        mAdapter.addMessage(unconfirmed);

        // When
        BaseMessage confirmed = new BaseMessage("server-id", "id", true, "from", new TextPayload("msg"), 0, false);
        mAdapter.addMessage(confirmed);

        // Then
        assertEquals(1, mAdapter.getItemCount());
        assertSame(confirmed, mAdapter.getMessage(0));
        verify(mDataObserver).onItemRangeChanged(0, 1, null);
    }

    @Test
    public void replaceMessage() {
        // Given
        BaseMessage original = new BaseMessage("server-id", "id", true, "from", new TextPayload("Old"), 0, false);
        mAdapter.addMessage(original);

        // When
        BaseMessage replacement = new BaseMessage("server-id", "id", true, "from", new TextPayload("New!"), 0, false);
        mAdapter.replaceMessage(replacement);

        // Then
        assertEquals(1, mAdapter.getItemCount());
        assertSame(replacement, mAdapter.getMessage(0));
        verify(mDataObserver).onItemRangeChanged(0, 1, null);
    }

    @Test
    public void toggleTimestampSameMessage() {
        // Given
        BaseMessage original = new BaseMessage("server-id", "id", true, "from", new TextPayload("Old"), 0, false);
        mAdapter.addMessage(original);

        // When
        mAdapter.toggleTimestampForMessage(original);

        // Then
        verify(mDataObserver).onItemRangeChanged(0, 1, null);
    }

    @Test
    public void toggleTimestampSameMessageTwice() {
        // Given
        BaseMessage original = new BaseMessage("server-id", "id", true, "from", new TextPayload("Old"), 0, false);
        mAdapter.addMessage(original);

        // When
        mAdapter.toggleTimestampForMessage(original);
        mAdapter.toggleTimestampForMessage(original); // Should hide timestamp

        // Then
        verify(mDataObserver, times(2)).onItemRangeChanged(0, 1, null);
    }

    @Test
    public void insertInCorrectPosition() {
        // Given
        List<BaseMessage> list = new ArrayList<>();
        list.add(BaseMessage.createTimestamp(0));
        list.add(BaseMessage.createTimestamp(10));
        list.add(BaseMessage.createTimestamp(20));
        mAdapter.addMessages(list);

        // When
        BaseMessage newMessage = BaseMessage.createUnconfirmedMessage("local-id", "from", new TextPayload("Test"), 15);
        mAdapter.addMessage(newMessage);

        // Then
        assertEquals(4, mAdapter.getItemCount());
        assertSame(newMessage, mAdapter.getMessage(2));
        verify(mDataObserver).onItemRangeInserted(2, 1);
    }

}
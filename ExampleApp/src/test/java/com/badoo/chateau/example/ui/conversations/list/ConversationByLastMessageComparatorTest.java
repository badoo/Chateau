package com.badoo.chateau.example.ui.conversations.list;

import com.badoo.chateau.example.data.model.ExampleConversation;
import com.badoo.chateau.example.data.model.ExampleMessage;
import com.badoo.chateau.ui.conversations.list.ConversationByLastMessageComparator;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class ConversationByLastMessageComparatorTest {

    private ConversationByLastMessageComparator mComparator;

    @Before
    public void setupComparator() {
        mComparator = new ConversationByLastMessageComparator();
    }

    @Test
    public void compareSameTimestamp() {
        int result = mComparator.compare(createConversation(0), createConversation(0));
        assertEquals(0, result);
    }

    @Test
    public void compareNewer() {
        int result = mComparator.compare(createConversation(0), createConversation(10));
        assertEquals(1, result);
    }

    @Test
    public void compareOlder() {
        int result = mComparator.compare(createConversation(10), createConversation(0));
        assertEquals(-1, result);
    }

    @Test
    public void compareNullLhs() {
        int result = mComparator.compare(createEmptyConversation(), createConversation(10));
        assertEquals(1, result);
    }

    @Test
    public void compareNullRhs() {
        int result = mComparator.compare(createConversation(10), createEmptyConversation());
        assertEquals(-1, result);
    }

    private ExampleConversation createEmptyConversation() {
        return new ExampleConversation("id", "", Collections.emptyList(), null, 0);
    }

    private ExampleConversation createConversation(long lastMessageTimestamp) {
        ExampleMessage lastMessage = ExampleMessage.createTimestamp(lastMessageTimestamp);
        return new ExampleConversation("id" + lastMessageTimestamp, "", Collections.emptyList(), lastMessage, 0);
    }
}
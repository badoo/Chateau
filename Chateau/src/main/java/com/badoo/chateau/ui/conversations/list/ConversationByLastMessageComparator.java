package com.badoo.chateau.ui.conversations.list;

import com.badoo.chateau.data.models.BaseConversation;
import com.badoo.chateau.data.models.BaseMessage;

import java.util.Comparator;

/**
 * Sorts conversations in descending order
 */
public class ConversationByLastMessageComparator implements Comparator<BaseConversation> {

    @Override
    public int compare(BaseConversation lhsConversation, BaseConversation rhsConversation) {
        final long lhs = lhsConversation.getLastMessage() != null ? ((BaseMessage) lhsConversation.getLastMessage()).getTimestamp() : 0;
        final long rhs = rhsConversation.getLastMessage() != null ? ((BaseMessage) rhsConversation.getLastMessage()).getTimestamp() : 0;
        return lhs > rhs ? -1 : (lhs == rhs ? 0 : 1);
    }

    @Override
    public boolean equals(Object object) {
        return object == this;
    }
}

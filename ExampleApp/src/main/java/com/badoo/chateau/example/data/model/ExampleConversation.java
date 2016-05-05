package com.badoo.chateau.example.data.model;

import android.support.annotation.NonNull;

import com.badoo.chateau.data.models.BaseConversation;
import com.badoo.chateau.data.models.BaseUser;

import java.util.List;

/**
 * Conversation model for the example app. Adds information about participants and the last message sent
 */
public class ExampleConversation extends BaseConversation {

    private List<BaseUser> mParticipants;
    private ExampleMessage mLastMessage;

    public ExampleConversation(@NonNull String id, @NonNull String name,
                               List<BaseUser> participants, ExampleMessage lastMessage, int unreadCount) {
        super(id, name, unreadCount);
        mParticipants = participants;
        mLastMessage = lastMessage;
    }

    public ExampleConversation(@NonNull String id) {
        super(id);
    }

    public List<BaseUser> getParticipants() {
        return mParticipants;
    }

    public ExampleMessage getLastMessage() {
        return mLastMessage;
    }
}

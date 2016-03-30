package com.badoo.chateau.data.models;

import com.badoo.chateau.core.model.Conversation;
import com.badoo.chateau.core.model.Message;

import java.util.Collections;
import java.util.List;

public class BaseConversation implements Conversation {

    private String mId;
    private String mName;
    private List<BaseUser> mParticipants;
    private Message mLastMessage;
    private int mUnreadCount;

    public BaseConversation(String id, String name, List<BaseUser> participants, Message lastMessage, int unreadCount) {
        mId = id;
        mName = name;
        mParticipants = participants;
        mLastMessage = lastMessage;
        mUnreadCount = unreadCount;
    }

    public BaseConversation(String id) {
        mId = id;
        mName = null;
        mParticipants = Collections.emptyList();
        mLastMessage = null;
        mUnreadCount = 0;
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public List<BaseUser> getParticipants() {
        return mParticipants;
    }

    public Message getLastMessage() {
        return mLastMessage;
    }

    public int getUnreadCount() {
        return mUnreadCount;
    }
}

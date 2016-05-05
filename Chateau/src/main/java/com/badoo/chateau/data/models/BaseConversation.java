package com.badoo.chateau.data.models;

import android.support.annotation.NonNull;

import com.badoo.chateau.core.model.Conversation;

public abstract class BaseConversation implements Conversation {

    private String mId;
    private String mName;
    private int mUnreadCount;

    public BaseConversation(@NonNull String id, String name, int unreadCount) {
        mId = id;
        mName = name;
        mUnreadCount = unreadCount;
    }

    public BaseConversation(@NonNull String id) {
        mId = id;
        mName = null;
        mUnreadCount = 0;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public int getUnreadCount() {
        return mUnreadCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseConversation)) return false;

        BaseConversation that = (BaseConversation) o;

        if (mUnreadCount != that.mUnreadCount) return false;
        if (mId != null ? !mId.equals(that.mId) : that.mId != null) return false;
        return mName != null ? mName.equals(that.mName) : that.mName == null;

    }

    @Override
    public int hashCode() {
        int result = mId != null ? mId.hashCode() : 0;
        result = 31 * result + (mName != null ? mName.hashCode() : 0);
        result = 31 * result + mUnreadCount;
        return result;
    }

    @Override
    public String toString() {
        return "BaseConversation{" +
            "mId='" + mId + '\'' +
            ", mName='" + mName + '\'' +
            ", mUnreadCount=" + mUnreadCount +
            '}';
    }
}

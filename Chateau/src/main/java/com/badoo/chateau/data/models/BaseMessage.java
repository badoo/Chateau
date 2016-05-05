package com.badoo.chateau.data.models;

import android.support.annotation.NonNull;

import com.badoo.chateau.core.model.Message;
import com.badoo.chateau.data.models.payloads.Payload;

/**
 * Base message type for all messages used in the app.  This object mainly contains meta data about the message with a single field
 * representing the message payload.
 */
public abstract class BaseMessage implements Message {

    protected static final String UNKNOWN_ID = "";

    private final String mId;
    private final long mTimestamp;
    private final String mFrom;
    private final Payload mPayload;
    private final boolean mFromMe;
    private final boolean mFailedToSend;

    public BaseMessage(@NonNull String id, boolean fromMe, @NonNull String from, @NonNull Payload payload, long timestamp, boolean failedToSend) {
        mId = id;
        mFromMe = fromMe;
        mTimestamp = timestamp;
        mFrom = from;
        mPayload = payload;
        mFailedToSend = failedToSend;
    }

    protected BaseMessage(@NonNull String id, boolean fromMe, @NonNull String from, @NonNull Payload payload, long timestamp) {
        this(id, fromMe, from, payload, timestamp, false);
    }

    public boolean isFromMe() {
        return mFromMe;
    }

    public Payload getPayload() {
        return mPayload;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    @NonNull
    public String getFrom() {
        return mFrom;
    }

    /**
     * Indicates that the message failed to be sent to the server. This should only be true when the message is also unconfirmed.
     */
    public boolean isFailedToSend() {
        return mFailedToSend;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseMessage)) return false;

        BaseMessage that = (BaseMessage) o;

        if (mTimestamp != that.mTimestamp) return false;
        if (mFromMe != that.mFromMe) return false;
        if (mFailedToSend != that.mFailedToSend) return false;
        if (mId != null ? !mId.equals(that.mId) : that.mId != null) return false;
        if (mFrom != null ? !mFrom.equals(that.mFrom) : that.mFrom != null) return false;
        return mPayload != null ? mPayload.equals(that.mPayload) : that.mPayload == null;

    }

    @Override
    public int hashCode() {
        int result = mId != null ? mId.hashCode() : 0;
        result = 31 * result + (int) (mTimestamp ^ (mTimestamp >>> 32));
        result = 31 * result + (mFrom != null ? mFrom.hashCode() : 0);
        result = 31 * result + (mPayload != null ? mPayload.hashCode() : 0);
        result = 31 * result + (mFromMe ? 1 : 0);
        result = 31 * result + (mFailedToSend ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BaseMessage{" +
            "mId='" + mId + '\'' +
            ", mTimestamp=" + mTimestamp +
            ", mFrom='" + mFrom + '\'' +
            ", mPayload=" + mPayload +
            '}';
    }
}

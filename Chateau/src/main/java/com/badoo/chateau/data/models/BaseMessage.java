package com.badoo.chateau.data.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.badoo.chateau.data.models.payloads.Payload;
import com.badoo.chateau.data.models.payloads.TimestampPayload;
import com.badoo.chateau.core.model.Message;

/**
 * Base message type for all messages used in the app.  This object mainly contains meta data about the message with a single field
 * representing the message payload.
 */
public class BaseMessage implements Message {

    private static final String UNKNOWN_ID = "";

    public static BaseMessage createOutgoingMessage(@NonNull Payload payload) {
        return new BaseMessage(UNKNOWN_ID, UNKNOWN_ID, true, "", payload, System.currentTimeMillis(), false);
    }

    public static BaseMessage createUnconfirmedMessage(@NonNull String localId, @NonNull String from, @NonNull Payload payload, long timestamp) {
        return new BaseMessage(UNKNOWN_ID, localId, true, from, payload, timestamp, false);
    }

    public static BaseMessage createTimestamp(long timestamp) {
        return new BaseMessage(UNKNOWN_ID, UNKNOWN_ID, false, UNKNOWN_ID, new TimestampPayload(), timestamp, false);
    }

    public static BaseMessage createFailedMessage(BaseMessage temporaryMessage) {
        return new BaseMessage(temporaryMessage.getId(), temporaryMessage.getLocalId(), temporaryMessage.isFromMe(), temporaryMessage.getFrom(), temporaryMessage.getPayload(), temporaryMessage.getTimestamp(), true);
    }

    private final String mId;
    private final long mTimestamp;
    private final String mLocalId;
    private final String mFrom;
    private final Payload mPayload;
    private final boolean mFromMe;
    private final boolean mFailedToSend;

    public BaseMessage(@NonNull String id, @Nullable String localId, boolean fromMe, @NonNull String from, @NonNull Payload payload, long timestamp, boolean failedToSend) {
        mId = id;
        mLocalId = localId;
        mFromMe = fromMe;
        mTimestamp = timestamp;
        mFrom = from;
        mPayload = payload;
        mFailedToSend = failedToSend;
    }

    protected BaseMessage(@NonNull String id, boolean fromMe, @NonNull String from, @NonNull Payload payload, long timestamp) {
        this(id, null, fromMe, from, payload, timestamp, false);
    }

    public boolean isFromMe() {
        return mFromMe;
    }

    public Payload getPayload() {
        return mPayload;
    }

    public String getLocalId() {
        return mLocalId;
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
     * Indicates that the message hasn't been confirmed by the server.  This is generally when the message has been sent, but a confirmation
     * has not yet been received form the server.
     */
    public boolean isUnconfirmed() {
        return UNKNOWN_ID.equals(mId);
    }

    /**
     * Indicates that the message failed to be sent to the server. This should only be true when the message is also unconfirmed.
     */
    public boolean isFailedToSend() {
        return mFailedToSend;
    }

    @Override
    public String toString() {
        return "BaseMessage{" +
            "mId='" + mId + '\'' +
            ", mTimestamp=" + mTimestamp +
            ", mLocalId='" + mLocalId + '\'' +
            ", mFrom='" + mFrom + '\'' +
            ", mPayload=" + mPayload +
            '}';
    }
}

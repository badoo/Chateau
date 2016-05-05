package com.badoo.chateau.example.data.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.badoo.chateau.data.models.BaseMessage;
import com.badoo.chateau.data.models.payloads.Payload;
import com.badoo.chateau.data.models.payloads.TimestampPayload;

/**
 * Message model for the example app
 */
public class ExampleMessage extends BaseMessage {

    private final String mLocalId;

    public static ExampleMessage createUnconfirmedMessage(@NonNull String localId, @NonNull String from, @NonNull Payload payload, long timestamp) {
        return new ExampleMessage(UNKNOWN_ID, localId, true, from, payload, timestamp, false);
    }

    public static ExampleMessage createTimestamp(long timestamp) {
        return new ExampleMessage(UNKNOWN_ID, UNKNOWN_ID, false, UNKNOWN_ID, new TimestampPayload(), timestamp, false);
    }

    public static ExampleMessage createFailedMessage(ExampleMessage temporaryMessage) {
        return new ExampleMessage(temporaryMessage.getId(), temporaryMessage.getLocalId(), temporaryMessage.isFromMe(), temporaryMessage.getFrom(), temporaryMessage.getPayload(), temporaryMessage.getTimestamp(), true);
    }

    public ExampleMessage(@NonNull String id, @Nullable String localId, boolean fromMe, @NonNull String from, @NonNull Payload payload, long timestamp, boolean failedToSend) {
        super(id, fromMe, from, payload, timestamp, failedToSend);
        mLocalId = localId;
    }

    protected ExampleMessage(@NonNull String id, boolean fromMe, @NonNull String from, @NonNull Payload payload, long timestamp) {
        super(id, fromMe, from, payload, timestamp);
        mLocalId = UNKNOWN_ID;
    }

    public String getLocalId() {
        return mLocalId;
    }

    /**
     * Indicates that the message hasn't been confirmed by the server.  This is generally when the message has been sent, but a confirmation
     * has not yet been received form the server.
     */
    public boolean isUnconfirmed() {
        return UNKNOWN_ID.equals(getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ExampleMessage that = (ExampleMessage) o;

        return mLocalId != null ? mLocalId.equals(that.mLocalId) : that.mLocalId == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (mLocalId != null ? mLocalId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return super.toString() + "+ExampleMessage{" +
            "mLocalId='" + mLocalId + '\'' +
            '}';
    }
}

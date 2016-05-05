package com.badoo.chateau.data.models.payloads;

/**
 * Dummy payload for timestamps in the messages list (chat)
 */
public class TimestampPayload implements Payload {

    // All needed data is already provided by BaseMessage. We just need this class to be able to register a renderer

    // These two are needed for the stable id calculation
    @Override
    public boolean equals(Object o) {
        return o instanceof TimestampPayload;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}

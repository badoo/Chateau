package com.badoo.chateau.data.models.payloads;

import android.support.annotation.NonNull;

/**
 * Payload containing just text
 */
public class TextPayload implements Payload {

    private final String mMessage;

    public TextPayload(@NonNull String message) {
        mMessage = message;
    }

    @NonNull
    public String getMessage() {
        return mMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TextPayload)) return false;

        TextPayload that = (TextPayload) o;

        return mMessage.equals(that.mMessage);

    }

    @Override
    public int hashCode() {
        return mMessage.hashCode();
    }

    @Override
    public String toString() {
        return "TextPayload{" +
            "mMessage='" + mMessage + '\'' +
            '}';
    }
}

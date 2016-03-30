package com.badoo.chateau.data.models.payloads;

import android.support.annotation.NonNull;

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
    public String toString() {
        return "TextPayload{" +
            "mMessage='" + mMessage + '\'' +
            '}';
    }
}

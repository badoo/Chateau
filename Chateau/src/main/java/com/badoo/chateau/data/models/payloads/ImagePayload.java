package com.badoo.chateau.data.models.payloads;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ImagePayload implements Payload {

    public static final String PLACEHOLDER = "placeholder";

    @NonNull
    private final String mImageUrl;
    @Nullable
    private final String mThumbnailUrl;
    @Nullable
    private final String mMessage;

    public ImagePayload(@NonNull String imageUrl, @Nullable  String thumbnailUrl, @Nullable String message) {
        mImageUrl = imageUrl;
        mThumbnailUrl = thumbnailUrl;
        mMessage = message;
    }

    public ImagePayload(@NonNull String imageUrl) {
        this(imageUrl, null, null);
    }

    @Nullable
    public String getMessage() {
        return mMessage;
    }

    @NonNull
    public String getImageUrl() {
        return mImageUrl;
    }

    @Nullable
    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    @Override
    public String toString() {
        return "ImagePayload{" +
            "mImageUrl='" + mImageUrl + '\'' +
            ", mMessage='" + mMessage + '\'' +
            '}';
    }
}

package com.badoo.chateau.data.models.payloads;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Payload containing an image and an optional message
 */
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImagePayload)) return false;

        ImagePayload that = (ImagePayload) o;

        if (!mImageUrl.equals(that.mImageUrl)) return false;
        if (mThumbnailUrl != null ? !mThumbnailUrl.equals(that.mThumbnailUrl) : that.mThumbnailUrl != null) return false;
        return mMessage != null ? mMessage.equals(that.mMessage) : that.mMessage == null;

    }

    @Override
    public int hashCode() {
        int result = mImageUrl.hashCode();
        result = 31 * result + (mThumbnailUrl != null ? mThumbnailUrl.hashCode() : 0);
        result = 31 * result + (mMessage != null ? mMessage.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ImagePayload{" +
            "mImageUrl='" + mImageUrl + '\'' +
            ", mThumbnailUrl='" + mThumbnailUrl + '\'' +
            ", mMessage='" + mMessage + '\'' +
            '}';
    }
}

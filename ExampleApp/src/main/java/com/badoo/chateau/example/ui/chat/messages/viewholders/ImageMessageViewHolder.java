package com.badoo.chateau.example.ui.chat.messages.viewholders;

import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.text.TextUtils;
import android.view.View;

import com.badoo.chateau.data.models.payloads.ImagePayload;
import com.badoo.chateau.example.R;
import com.badoo.chateau.example.data.model.ExampleMessage;
import com.badoo.chateau.example.ui.util.ImageLoadingDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;

public class ImageMessageViewHolder extends ChatBubbleViewHolder<ImagePayload> {

    private final SimpleDraweeView mMessageImage;
    private final ContentLoadingProgressBar mImageProgress;

    public ImageMessageViewHolder(@NonNull View itemView) {
        super(itemView);
        mMessageImage = (SimpleDraweeView) itemView.findViewById(R.id.message_image);
        mImageProgress = (ContentLoadingProgressBar) itemView.findViewById(R.id.message_imageProgress);

        final Resources resources = itemView.getResources();
        final int shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime);
        final GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(resources)
            .setFadeDuration(shortAnimTime)
            .setProgressBarImage(new ImageLoadingDrawable(resources))
            .setFailureImage(resources.getDrawable(R.drawable.ic_upload_failed), ScalingUtils.ScaleType.CENTER_INSIDE)
            .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
            .build();
        mMessageImage.setHierarchy(hierarchy);
    }

    @Override
    protected void bindPayload(ExampleMessage message, ImagePayload payload) {
        final String imageUri;
        if (!TextUtils.isEmpty(payload.getThumbnailUrl())) {
            imageUri = payload.getThumbnailUrl();
        }
        else {
            imageUri = payload.getImageUrl();
        }
        if (!TextUtils.isEmpty(imageUri) && !ImagePayload.PLACEHOLDER.equals(imageUri)) {
            mMessageImage.setImageURI(Uri.parse(imageUri));
            mImageProgress.setVisibility(View.GONE);
        }
        else {
            //noinspection deprecation
            mMessageImage.setImageDrawable(null);
            mImageProgress.setVisibility(View.VISIBLE);
            mImageProgress.show();
        }
    }

}

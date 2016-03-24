package com.badoo.chateau.example.ui.chat.messages.viewholders;

import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badoo.chateau.data.models.BaseMessage;
import com.badoo.chateau.data.models.payloads.ImagePayload;
import com.badoo.chateau.example.R;
import com.badoo.chateau.example.ui.util.BaseMessageViewHolder;
import com.badoo.chateau.example.ui.util.ImageLoadingDrawable;
import com.badoo.chateau.example.ui.widgets.TintableBackgroundLinearLayout;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;

public class ImageMessageViewHolder extends BaseMessageViewHolder<ImagePayload> {

    private final LinearLayout mRoot;
    private final TextView mMessageText;
    private final TintableBackgroundLinearLayout mBackground;
    private final SimpleDraweeView mMessageImage;
    private final ContentLoadingProgressBar mImageProgress;
    private final int mMargin;

    public ImageMessageViewHolder(@NonNull View itemView) {
        super(itemView);
        mRoot = (LinearLayout) itemView;
        mMessageText = (TextView) itemView.findViewById(R.id.message_text);
        mMessageImage = (SimpleDraweeView) itemView.findViewById(R.id.message_image);
        mImageProgress = (ContentLoadingProgressBar) itemView.findViewById(R.id.message_imageProgress);
        mBackground = (TintableBackgroundLinearLayout) itemView.findViewById(R.id.message_background);

        final Resources resources = itemView.getResources();
        mMargin = resources.getDimensionPixelSize(R.dimen.chatBubbleMargin);
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
    protected void bindPayload(BaseMessage message, ImagePayload payload) {
        mMessageText.setVisibility(TextUtils.isEmpty(payload.getMessage()) ? View.GONE : View.VISIBLE);
        mMessageText.setText((payload.getMessage()));

        String imageUri = null;
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

        final boolean fromMe = message.isFromMe();
        // The tint helper needs a state list so we use one where the enabled state is "from me" and the disabled state is "from other person"
        mBackground.setEnabled(fromMe);
        mBackground.setBackgroundResource(fromMe ? R.drawable.bg_chat_bubble_right : R.drawable.bg_chat_bubble_left);

        mRoot.setGravity(fromMe ? Gravity.RIGHT : Gravity.LEFT);
        final LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mBackground.getLayoutParams();
        params.leftMargin = fromMe ? mMargin : 0;
        params.rightMargin = fromMe ? 0 : mMargin;

    }

}

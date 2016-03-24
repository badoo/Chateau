package com.badoo.chateau.example.ui.chat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.badoo.chateau.example.R;
import com.badoo.chateau.example.ui.util.ImageLoadingDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;

public class FullScreenImageActivity extends AppCompatActivity {

    private static final String EXTRA_IMAGE_URI = ChatActivity.class.getName() + "extra:imageUri";

    public static Intent create(@NonNull Context context, @NonNull Uri imageUri) {
        final Intent intent = new Intent(context, FullScreenImageActivity.class);
        intent.putExtra(EXTRA_IMAGE_URI, imageUri);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        final SimpleDraweeView mImageView = (SimpleDraweeView) findViewById(R.id.view_image_image);
        final int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        final GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(getResources())
            .setFadeDuration(shortAnimTime)
            .setProgressBarImage(new ImageLoadingDrawable(getResources()))
            .setFailureImage(getResources().getDrawable(R.drawable.ic_upload_failed), ScalingUtils.ScaleType.CENTER_INSIDE)
            .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
            .build();
        mImageView.setHierarchy(hierarchy);

        mImageView.setImageURI(getIntent().getParcelableExtra(EXTRA_IMAGE_URI));
    }
}

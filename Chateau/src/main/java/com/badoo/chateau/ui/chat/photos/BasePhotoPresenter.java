package com.badoo.chateau.ui.chat.photos;

import android.support.annotation.NonNull;

import com.badoo.barf.mvp.BaseRxPresenter;

/**
 * Base implementation of the PhotoPresenter interface.
 */
public class BasePhotoPresenter extends BaseRxPresenter implements PhotoPresenter {

    @NonNull
    private final PhotoFlowListener mFlowListener;

    public BasePhotoPresenter(@NonNull PhotoFlowListener flowListener) {
        mFlowListener = flowListener;
    }

    @Override
    public void onPickPhoto() {
        mFlowListener.requestPickPhoto();
    }

    @Override
    public void onTakePhoto() {
        mFlowListener.requestTakePhoto();
    }
}

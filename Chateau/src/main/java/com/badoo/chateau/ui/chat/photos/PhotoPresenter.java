package com.badoo.chateau.ui.chat.photos;

import com.badoo.barf.mvp.FlowListener;
import com.badoo.barf.mvp.MvpPresenter;
import com.badoo.barf.mvp.MvpView;

/**
 * Presenter for sending different types of photos.
 */
public interface PhotoPresenter extends MvpPresenter {

    /**
     * Called when the user starts the "Send photo from album" flow
     */
    void onPickPhoto();

    /**
     * Called when the user starts the "Send photo from album" flow
     */
    void onTakePhoto();

    interface PhotoView extends MvpView{

    }

    interface PhotoFlowListener extends FlowListener {

        /**
         * Request an image to be picked from gallery
         */
        void requestPickPhoto();

        /**
         * Request an image to be taken using the camera
         */
        void requestTakePhoto();

    }
}

package com.badoo.chateau.ui.chat.input;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.badoo.barf.mvp.FlowListener;
import com.badoo.barf.mvp.MvpView;
import com.badoo.barf.mvp.MvpPresenter;

/**
 * Presenter managing the input part (text, images, etc) of the chat screen
 */
public interface ChatInputPresenter extends MvpPresenter {

    /**
     * Called when a message has been entered and is ready to be sent
     */
    void onSendMessage(@NonNull String message);

    /**
     * Called when the user starts the "Send photo from album" flow
     */
    void onPickImage();

    /**
     * Called when the user starts the "Send photo from album" flow
     */
    void onTakePhoto();

    /**
     * Called after either a photo was picked from gallery or from the camera
     */
    void onSendImage(@NonNull Uri uri);

    /**
     * Called when the user is typing a message, should be throttled (don't call for every keystroke)
     */
    void onUserTyping();

    interface ChatInputView extends MvpView {

        /**
         * Clears the enter text in the text field
         */
        void clearText();

    }

    interface ChatInputFlowListener extends FlowListener {

        /**
         * Request an image to be picked from gallery
         */
        void requestPickLocalImageForMessage();

        /**
         * Request an image to be taken using the camera
         */
        void requestTakePhotoForMessage();

    }
}

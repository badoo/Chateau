package com.badoo.chateau.example.ui.chat.photos;

import android.support.annotation.NonNull;

import com.badoo.barf.mvp.PresenterFactory;
import com.badoo.chateau.example.R;
import com.badoo.chateau.example.ui.widgets.ChatTextInputView;
import com.badoo.chateau.extras.ViewFinder;
import com.badoo.chateau.ui.chat.photos.PhotoPresenter;

public class PhotoViewImpl implements PhotoPresenter.PhotoView {

    public PhotoViewImpl(@NonNull ViewFinder viewFinder,
                         @NonNull PresenterFactory<PhotoPresenter.PhotoView, PhotoPresenter> presenterFactory) {
        PhotoPresenter presenter = presenterFactory.init(this);
        ChatTextInputView input = viewFinder.findViewById(R.id.chat_input);
        input.setOnActionItemClickedListener(item -> {
            if (item.getItemId() == R.id.action_attachPhoto) {
                presenter.onPickPhoto();
            }
            else if (item.getItemId() == R.id.action_takePhoto) {
                presenter.onTakePhoto();
            }
            return true;
        });
    }
}

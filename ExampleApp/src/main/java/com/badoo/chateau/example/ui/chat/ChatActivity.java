package com.badoo.chateau.example.ui.chat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;

import com.badoo.chateau.example.R;
import com.badoo.chateau.example.ui.BaseActivity;
import com.badoo.chateau.example.ui.Injector;
import com.badoo.chateau.example.ui.chat.input.ChatInputViewImpl;
import com.badoo.chateau.example.ui.chat.messages.MessageListViewImpl;
import com.badoo.chateau.extras.ViewFinder;
import com.badoo.chateau.ui.chat.input.ChatInputPresenter;
import com.badoo.chateau.ui.chat.input.ChatInputPresenterImpl;
import com.badoo.chateau.ui.chat.input.ChatInputView;
import com.badoo.chateau.ui.chat.messages.MessageListPresenter;
import com.badoo.chateau.ui.chat.messages.MessageListPresenterImpl;
import com.badoo.chateau.ui.chat.messages.MessageListView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ChatActivity extends BaseActivity implements ChatInputPresenter.ChatInputFlowListener, MessageListPresenter.MessageListFlowListener {

    public static class DefaultConfiguration extends Injector.BaseConfiguration<ChatActivity> {

        @Override
        public void inject(ChatActivity activity) {
            final ViewFinder finder = ViewFinder.from(activity);
            final String chatId = activity.getIntent().getStringExtra(EXTRA_CHAT_ID);

            ChatInputView chatInputView = new ChatInputViewImpl(finder);
            final ChatInputPresenter inputPresenter = createChatInputPresenter(chatId);
            bind(chatInputView, inputPresenter, activity);
            activity.setInputPresenter(inputPresenter);

            MessageListView messageListView = createMessageListView(activity);
            final MessageListPresenter messageListPresenter = createMessageListPresenter(chatId);
            bind(messageListView, messageListPresenter, activity);
            activity.setMessageListPresenter(messageListPresenter);
        }

        protected MessageListPresenter createMessageListPresenter(String chatId) {
            return new MessageListPresenterImpl(chatId);
        }

        protected MessageListView createMessageListView(ChatActivity activity) {
            return new MessageListViewImpl(ViewFinder.from(activity), activity.getSupportActionBar());
        }

        protected ChatInputPresenter createChatInputPresenter(String chatId) {
            return new ChatInputPresenterImpl(chatId);
        }
    }

    private static final String EXTRA_CHAT_ID = ChatActivity.class.getName() + "extra:chatId";
    private static final String EXTRA_CHAT_NAME = ChatActivity.class.getName() + "extra:chatName";
    private static final String SIS_PHOTO_LOCATION = ChatActivity.class.getName() + "sis:photoLocation";

    private static final int REQ_PICK_IMAGE = 542;
    private static final int REQ_TAKE_PHOTO = 543;

    public static Intent create(@NonNull Context context, @NonNull String chatId, @NonNull String chatName) {
        final Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(EXTRA_CHAT_ID, chatId);
        intent.putExtra(EXTRA_CHAT_NAME, chatName);
        return intent;
    }

    private ChatInputPresenter mInputPresenter;
    private Uri mPhotoLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setTitle(getIntent().getStringExtra(EXTRA_CHAT_NAME));

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null) {
            mPhotoLocation = savedInstanceState.getParcelable(SIS_PHOTO_LOCATION);
        }
        Injector.inject(this);
    }

    void setInputPresenter(@NonNull ChatInputPresenter chatInputPresenter) {
        mInputPresenter = chatInputPresenter;
        registerPresenter(mInputPresenter);
    }

    void setMessageListPresenter(@NonNull MessageListPresenter messageListPresenter) {
        registerPresenter(messageListPresenter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                mInputPresenter.onSendImage(data.getData());
            }
        }
        if (requestCode == REQ_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                mInputPresenter.onSendImage(mPhotoLocation);
                mPhotoLocation = null;
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SIS_PHOTO_LOCATION, mPhotoLocation);
    }

    @Override
    public void pickLocalImageForMessage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQ_PICK_IMAGE);
    }

    @Override
    public void takePhotoForMessage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            final File photoFile;
            try {
                photoFile = createImageFile();
            }
            catch (IOException e) {
                // Error occurred while creating the File
                Log.e("ChatActivity", "Unable to create photo file", e);
                return;
            }
            // Continue only if the File was successfully created
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            startActivityForResult(intent, REQ_TAKE_PHOTO);
        }
    }

    private File createImageFile() throws IOException {
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        final String imageFileName = timeStamp + "_chateau";
        final File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        final File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mPhotoLocation = Uri.fromFile(image);
        return image;
    }

    @Override
    public void openImage(@NonNull Uri imageUri) {
        startActivity(FullScreenImageActivity.create(ChatActivity.this, imageUri));
    }

}

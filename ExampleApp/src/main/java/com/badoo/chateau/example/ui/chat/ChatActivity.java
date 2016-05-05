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

import com.badoo.barf.mvp.PresenterFactory;
import com.badoo.chateau.core.usecases.conversations.GetConversation;
import com.badoo.chateau.core.usecases.conversations.MarkConversationRead;
import com.badoo.chateau.core.usecases.istyping.SendUserIsTyping;
import com.badoo.chateau.core.usecases.istyping.SubscribeToUsersTyping;
import com.badoo.chateau.core.usecases.messages.LoadMessages;
import com.badoo.chateau.core.usecases.messages.SendMessage;
import com.badoo.chateau.core.usecases.messages.SubscribeToMessages;
import com.badoo.chateau.example.R;
import com.badoo.chateau.example.data.model.ExampleConversation;
import com.badoo.chateau.example.data.model.ExampleMessage;
import com.badoo.chateau.example.ui.BaseActivity;
import com.badoo.chateau.example.ui.ExampleConfiguration;
import com.badoo.chateau.example.ui.Injector;
import com.badoo.chateau.example.ui.chat.input.ChatInputViewImpl;
import com.badoo.chateau.example.ui.chat.messages.ExampleMessageListView;
import com.badoo.chateau.extras.ViewFinder;
import com.badoo.chateau.ui.chat.input.ChatInputPresenter;
import com.badoo.chateau.ui.chat.input.ChatInputPresenterImpl;
import com.badoo.chateau.ui.chat.messages.BaseMessageListPresenter;
import com.badoo.chateau.ui.chat.messages.MessageListPresenter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.badoo.chateau.ui.chat.input.ChatInputPresenter.ChatInputFlowListener;
import static com.badoo.chateau.ui.chat.input.ChatInputPresenter.ChatInputView;
import static com.badoo.chateau.ui.chat.messages.MessageListPresenter.MessageListFlowListener;
import static com.badoo.chateau.ui.chat.messages.MessageListPresenter.MessageListView;


public class ChatActivity extends BaseActivity implements ChatInputFlowListener, MessageListFlowListener {

    public static class DefaultConfiguration extends ExampleConfiguration<ChatActivity> {

        @Override
        public void inject(ChatActivity activity) {
            final String chatId = activity.getIntent().getStringExtra(EXTRA_CHAT_ID);
            createChatInputView(activity, chatId);
            createMessageListView(activity, chatId);
        }

        protected ChatInputView createChatInputView(@NonNull ChatActivity activity, @NonNull String chatId) {
            final PresenterFactory<ChatInputView, ChatInputPresenter> presenterFactory = new PresenterFactory<>(v -> createChatInputPresenter(v, activity, chatId));
            final ChatInputViewImpl chatInputView = new ChatInputViewImpl(ViewFinder.from(activity), presenterFactory);
            activity.registerPresenter(presenterFactory.get());
            activity.setInputPresenter(presenterFactory.get());
            return chatInputView;
        }

        protected ChatInputPresenter createChatInputPresenter(@NonNull ChatInputView view, @NonNull ChatInputFlowListener flowListener, @NonNull String chatId) {
            return new ChatInputPresenterImpl(chatId, view, flowListener, new SendMessage(getMessageRepo()), new SendUserIsTyping(getIsTypingRepo()));
        }

        protected ExampleMessageListView createMessageListView(@NonNull ChatActivity activity, @NonNull String chatId) {
            final PresenterFactory<MessageListView<ExampleMessage, ExampleConversation>, MessageListPresenter> presenterFactory = new PresenterFactory<>(v -> createMessageListPresenter(v, activity, chatId));
            final ExampleMessageListView view = new ExampleMessageListView(ViewFinder.from(activity), activity.getSupportActionBar(), presenterFactory);
            activity.registerPresenter(presenterFactory.get());
            return view;
        }

        protected MessageListPresenter createMessageListPresenter(@NonNull MessageListView<ExampleMessage, ExampleConversation> view, @NonNull MessageListFlowListener flowListener, @NonNull String chatId) {
            return new BaseMessageListPresenter<>(chatId, view, flowListener,
                new LoadMessages<>(getMessageRepo()),
                new SubscribeToMessages<>(getMessageRepo()),
                new MarkConversationRead(getConversationRepo()),
                new GetConversation<>(getConversationRepo()),
                new SubscribeToUsersTyping<>(getIsTypingRepo()));
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
    public void requestPickLocalImageForMessage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQ_PICK_IMAGE);
    }

    @Override
    public void requestTakePhotoForMessage() {
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
    public void requestOpenImage(@NonNull Uri imageUri) {
        startActivity(FullScreenImageActivity.create(ChatActivity.this, imageUri));
    }

}

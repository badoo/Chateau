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

import com.badoo.barf.data.repo.Repository;
import com.badoo.barf.mvp.PresenterFactory;
import com.badoo.chateau.core.usecases.conversations.GetConversation;
import com.badoo.chateau.core.usecases.conversations.MarkConversationRead;
import com.badoo.chateau.core.usecases.istyping.SendUserIsTyping;
import com.badoo.chateau.core.usecases.istyping.SubscribeToUsersTyping;
import com.badoo.chateau.core.usecases.messages.LoadMessages;
import com.badoo.chateau.core.usecases.messages.SendMessage;
import com.badoo.chateau.core.usecases.messages.SubscribeToMessageUpdates;
import com.badoo.chateau.example.R;
import com.badoo.chateau.example.data.model.ExampleMessage;
import com.badoo.chateau.example.data.model.ExampleUser;
import com.badoo.chateau.example.ui.BaseActivity;
import com.badoo.chateau.example.ui.ExampleConfiguration;
import com.badoo.chateau.example.ui.Injector;
import com.badoo.chateau.example.ui.chat.info.ExampleChatInfoPresenter;
import com.badoo.chateau.example.ui.chat.info.ExampleChatInfoPresenter.ExampleChatInfoView;
import com.badoo.chateau.example.ui.chat.info.ExampleChatInfoPresenterImpl;
import com.badoo.chateau.example.ui.chat.info.ExampleChatInfoViewImpl;
import com.badoo.chateau.example.ui.chat.input.ChatInputViewImpl;
import com.badoo.chateau.example.ui.chat.input.ExampleChatInputPresenter;
import com.badoo.chateau.example.ui.chat.input.ExampleChatInputPresenterImpl;
import com.badoo.chateau.example.ui.chat.istyping.ExampleIsTypingPresenter;
import com.badoo.chateau.example.ui.chat.istyping.ExampleIsTypingPresenter.ExampleIsTypingView;
import com.badoo.chateau.example.ui.chat.istyping.ExampleIsTypingPresenterImpl;
import com.badoo.chateau.example.ui.chat.istyping.ExampleIsTypingViewImpl;
import com.badoo.chateau.example.ui.chat.messages.ExampleMessageListPresenter;
import com.badoo.chateau.example.ui.chat.messages.ExampleMessageListPresenter.ExampleMessageListFlowListener;
import com.badoo.chateau.example.ui.chat.messages.ExampleMessageListPresenterImpl;
import com.badoo.chateau.example.ui.chat.messages.ExampleMessageListView;
import com.badoo.chateau.example.ui.chat.messages.ExampleMessageListViewImpl;
import com.badoo.chateau.example.ui.chat.photos.PhotoViewImpl;
import com.badoo.chateau.extras.ViewFinder;
import com.badoo.chateau.ui.chat.input.ChatInputPresenter;
import com.badoo.chateau.ui.chat.photos.BasePhotoPresenter;
import com.badoo.chateau.ui.chat.photos.PhotoPresenter;
import com.badoo.chateau.ui.chat.photos.PhotoPresenter.PhotoFlowListener;
import com.badoo.chateau.ui.chat.photos.PhotoPresenter.PhotoView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.badoo.chateau.ui.chat.input.ChatInputPresenter.ChatInputView;


public class ChatActivity extends BaseActivity implements ExampleMessageListFlowListener, PhotoFlowListener {

    public static class DefaultConfiguration extends ExampleConfiguration<ChatActivity> {

        @Override
        public void inject(ChatActivity activity) {
            final String conversationId = activity.getIntent().getStringExtra(EXTRA_CONVERSATION_ID);
            createChatInputView(activity, conversationId);
            createMessageListView(activity, conversationId);
            createPhotoView(activity);
            createIsTypingView(activity, conversationId);
            createChatInfoView(activity, conversationId);
        }

        protected ChatInputView createChatInputView(@NonNull ChatActivity activity, @NonNull String conversationId) {
            final PresenterFactory<ChatInputView, ChatInputPresenter<ExampleMessage>> presenterFactory = new PresenterFactory<>(v -> createTextInputPresenter(v, conversationId));
            final ChatInputViewImpl chatInputView = new ChatInputViewImpl(conversationId, ViewFinder.from(activity), presenterFactory);
            activity.registerPresenter(presenterFactory.get());
            activity.setInputPresenter(presenterFactory.get());
            return chatInputView;
        }

        protected PhotoView createPhotoView(@NonNull ChatActivity activity) {
            final PresenterFactory<PhotoView, PhotoPresenter> presenterFactory = new PresenterFactory<>((PresenterFactory.PresenterFactoryDelegate<PhotoView, PhotoPresenter>) v -> createPhotoPresenter(activity));
            final PhotoView photoView = new PhotoViewImpl(ViewFinder.from(activity), presenterFactory);
            activity.registerPresenter(presenterFactory.get());
            return photoView;
        }

        protected ExampleChatInputPresenter createTextInputPresenter(@NonNull ChatInputView view, @NonNull String conversationId) {
            return new ExampleChatInputPresenterImpl(conversationId, view, new SendMessage<>(getMessageRepo()));
        }

        protected PhotoPresenter createPhotoPresenter(@NonNull PhotoFlowListener flowListener) {
            return new BasePhotoPresenter(flowListener);
        }

        protected ExampleChatInfoView createChatInfoView(@NonNull ChatActivity activity, @NonNull String conversationId) {
            final PresenterFactory<ExampleChatInfoView, ExampleChatInfoPresenter> factory = new PresenterFactory<>(v -> createChatInfoPresenter(v, conversationId));
            final ExampleChatInfoView view = new ExampleChatInfoViewImpl(factory, activity.getSupportActionBar());
            activity.registerPresenter(factory.get());
            return view;
        }

        protected ExampleChatInfoPresenter createChatInfoPresenter(@NonNull ExampleChatInfoView v, @NonNull String conversationId) {
            return new ExampleChatInfoPresenterImpl(v, conversationId, new GetConversation<>(getConversationRepo()));
        }

        protected ExampleIsTypingView createIsTypingView(@NonNull ChatActivity activity, @NonNull String conversationId) {
            final PresenterFactory<ExampleIsTypingView, ExampleIsTypingPresenter> factory = new PresenterFactory<>(v -> createIsTypingPresenter(v, conversationId));
            final ExampleIsTypingViewImpl view = new ExampleIsTypingViewImpl(factory,
                ViewFinder.from(activity),
                activity.getSupportActionBar());
            activity.registerPresenter(factory.get());
            return view;
        }

        protected ExampleIsTypingPresenter createIsTypingPresenter(@NonNull ExampleIsTypingView view, @NonNull String conversationId) {
            Repository<ExampleUser> repo = getIsTypingRepo();
            return new ExampleIsTypingPresenterImpl(view, conversationId,
                new SubscribeToUsersTyping<>(repo),
                new SendUserIsTyping(repo));
        }

        protected ExampleMessageListView createMessageListView(@NonNull ChatActivity activity, @NonNull String chatId) {
            final PresenterFactory<ExampleMessageListView, ExampleMessageListPresenter> presenterFactory = new PresenterFactory<>(v -> createMessageListPresenter(v, activity, chatId));
            final ExampleMessageListViewImpl view = new ExampleMessageListViewImpl(ViewFinder.from(activity), presenterFactory);
            activity.registerPresenter(presenterFactory.get());
            return view;
        }

        protected ExampleMessageListPresenter createMessageListPresenter(@NonNull ExampleMessageListView view, @NonNull ExampleMessageListFlowListener flowListener, @NonNull String chatId) {
            return new ExampleMessageListPresenterImpl(chatId, view, flowListener,
                new LoadMessages<>(getMessageRepo()),
                new SubscribeToMessageUpdates<>(getMessageRepo()),
                new MarkConversationRead(getConversationRepo()),
                new SendMessage<>(getMessageRepo()));
        }
    }

    private static final String EXTRA_CONVERSATION_ID = ChatActivity.class.getName() + "extra:chatId";
    private static final String EXTRA_CHAT_NAME = ChatActivity.class.getName() + "extra:chatName";
    private static final String SIS_PHOTO_LOCATION = ChatActivity.class.getName() + "sis:photoLocation";

    private static final int REQ_PICK_IMAGE = 542;
    private static final int REQ_TAKE_PHOTO = 543;

    public static Intent create(@NonNull Context context, @NonNull String chatId, @NonNull String chatName) {
        final Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(EXTRA_CONVERSATION_ID, chatId);
        intent.putExtra(EXTRA_CHAT_NAME, chatName);
        return intent;
    }

    private ChatInputPresenter<ExampleMessage> mInputPresenter;
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

    void setInputPresenter(@NonNull ChatInputPresenter<ExampleMessage> chatInputPresenter) {
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
        final String conversationId = getIntent().getStringExtra(EXTRA_CONVERSATION_ID);
        if (requestCode == REQ_PICK_IMAGE) {
            if (resultCode == RESULT_OK && data.getData() != null) {
                mInputPresenter.onSendMessage(ExampleMessage.createOutgoingPhotoMessage(conversationId, data.getData().toString()));
            }
        }
        else if (requestCode == REQ_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                mInputPresenter.onSendMessage(ExampleMessage.createOutgoingPhotoMessage(conversationId, mPhotoLocation.toString()));
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
    public void requestPickPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQ_PICK_IMAGE);
    }

    @Override
    public void requestTakePhoto() {
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

package com.badoo.chateau.example.data.repos.messages;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.badoo.chateau.data.models.BaseMessage;
import com.badoo.chateau.data.models.payloads.ImagePayload;
import com.badoo.chateau.data.models.payloads.Payload;
import com.badoo.chateau.data.models.payloads.TextPayload;
import com.badoo.chateau.example.data.util.ParseHelper;
import com.badoo.chateau.example.data.util.ParseUtils;
import com.badoo.chateau.core.model.Message;
import com.badoo.chateau.core.repos.messages.MessageDataSource;
import com.badoo.chateau.core.repos.messages.MessageQuery;
import com.badoo.chateau.core.repos.messages.MessageQuery.GetMessages;
import com.badoo.chateau.core.repos.messages.MessageQuery.SendMessage;
import com.badoo.chateau.core.repos.messages.MessageQuery.SubscribeToNewMessagesForConversation;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

import com.badoo.chateau.example.data.util.ParseUtils.ChatTable;
import com.badoo.chateau.example.data.util.ParseUtils.MessagesTable;

public class ParseMessageDataSource implements MessageDataSource {

    public final static int MAX_CHUNK_SIZE = 20;

    @SuppressWarnings("unused")
    private static final String TAG = ParseMessageDataSource.class.getSimpleName();

    private final Map<String, PublishSubject<Message>> mNewMessagePublishers = new HashMap<>();
    private final Map<String, PublishSubject<Message>> mUpdatedMessagePublishers = new HashMap<>();

    @NonNull
    private final ImageUploader mImageUploader;
    @NonNull
    private final ParseHelper mParseHelper;
    // In order to guarantee that any new temporary message is inserted as the last entry of the conversation (even if the device
    // time is incorrectly set) we keep track of the timestamp of the last message (across loaded conversations).
    private long mLastMessageTimestamp;

    private Action1<List<ParseObject>> mSortMessagesAscending = parseObjects -> Collections.sort(parseObjects, (lhs, rhs) -> {
        // Sort Acceding.
        final long lhsTimestamp = lhs.getCreatedAt().getTime();
        final long rhsTimestamp = rhs.getCreatedAt().getTime();
        return lhsTimestamp < rhsTimestamp ? -1 : (lhsTimestamp == rhsTimestamp ? 0 : 1);
    });

    public ParseMessageDataSource(@NonNull ImageUploader imageUploader, @NonNull ParseHelper parseHelper) {
        mImageUploader = imageUploader;
        mParseHelper = parseHelper;
    }

    @NonNull
    @Override
    public Observable<Message> getMessages(@NonNull GetMessages getMessages) {
        final ParseQuery<ParseObject> query = new ParseQuery<>(MessagesTable.NAME);
        query.whereEqualTo(MessagesTable.Fields.CHAT, ParseObject.createWithoutData(ChatTable.NAME, getMessages.getChatId()));
        query.addDescendingOrder(MessagesTable.Fields.CREATED_AT);
        if (getMessages.getChunkBefore() != null) {
            query.whereLessThan(MessagesTable.Fields.CREATED_AT, new Date(((BaseMessage) getMessages.getChunkBefore()).getTimestamp()));
        }
        query.setLimit(MAX_CHUNK_SIZE);

        query.include(MessagesTable.Fields.IMAGE);

        return mParseHelper.find(query)
            .doOnNext(mSortMessagesAscending)
            .flatMap(Observable::from)
            .map(ParseUtils::baseMessageFromParseObject)
            .doOnNext(message -> mLastMessageTimestamp = Math.max(mLastMessageTimestamp, ((BaseMessage) message).getTimestamp()));
    }

    @NonNull
    @Override
    public Observable<Message> subscribeToNewMessage(@NonNull SubscribeToNewMessagesForConversation query) {
        return getNewMessagePublisher(query.getChatId());
    }

    @NonNull
    @Override
    public Observable<Message> subscribeToUpdatedMessage(@NonNull MessageQuery.GetUpdatedMessagesForConversation query) {
        return getUpdatedMessagePublisher(query.getChatId());
    }

    @Override
    public void sendMessage(@NonNull SendMessage sendMessage) {
        final ParseObject msg = ParseObject.create(MessagesTable.NAME);
        final BaseMessage message = (BaseMessage) sendMessage.getMessage();
        final Payload payload = message.getPayload();
        final String localId = generateLocalId();

        final String chatId = sendMessage.getChatId();
        final Action0 onSuccess;
        if (payload instanceof TextPayload) {
            onSuccess = handleTextPayload(msg, (TextPayload) payload, chatId);
        }
        else if (payload instanceof ImagePayload) {
            onSuccess = handleImagePayload(msg, (ImagePayload) payload, localId);
        }
        else {
            throw new IllegalArgumentException("Unsupported message type: " + message);
        }

        msg.put(MessagesTable.Fields.CHAT, ParseObject.createWithoutData(ChatTable.NAME, chatId));
        // Need to ensure the message sent back has the generated local id so it can be matched with responses form the server
        msg.put(MessagesTable.Fields.LOCAL_ID, localId);
        // Give it a timestamp that ensured that it ends up last (this will be replaced by the real server side timestamp)
        mLastMessageTimestamp = Math.max(System.currentTimeMillis(), mLastMessageTimestamp + 1);

        final BaseMessage unconfirmedMessage = BaseMessage.createUnconfirmedMessage(localId, mParseHelper.getCurrentUser().getObjectId(), message.getPayload(), mLastMessageTimestamp);
        getNewMessagePublisher(chatId).onNext(unconfirmedMessage);

        uploadMessage(msg, onSuccess, e -> getNewMessagePublisher(chatId).onNext(BaseMessage.createFailedMessage(unconfirmedMessage)));
    }


    /**
     * Configure the parse object for a text payload an return the action to run on successful sending of the message
     */
    @NonNull
    private Action0 handleTextPayload(@NonNull ParseObject msg, @NonNull TextPayload payload, @NonNull String chatId) {
        Action0 onSuccess;
        msg.put(MessagesTable.Fields.TYPE, MessagesTable.Types.TEXT);
        msg.put(MessagesTable.Fields.MESSAGE, payload.getMessage());
        onSuccess = () -> {
            mLastMessageTimestamp = Math.max(msg.getCreatedAt().getTime(), mLastMessageTimestamp);
            // Publish the real message back to the listening presenter layer after it is successfully saved (so that it can replace the temporary message)
            getNewMessagePublisher(chatId).onNext(ParseUtils.baseMessageFromParseObject(msg));
        };
        return onSuccess;
    }

    /**
     * Configure the parse object for a image payload an return the action to run on successful sending of the message
     */
    @NonNull
    private Action0 handleImagePayload(@NonNull ParseObject msg, @NonNull ImagePayload payload, @NonNull String localId) {
        Action0 onSuccess;
        msg.put(MessagesTable.Fields.TYPE, MessagesTable.Types.IMAGE);
        if (payload.getMessage() != null) {
            msg.put(MessagesTable.Fields.MESSAGE, payload.getMessage());
        }
        final Uri imageUri = Uri.parse(payload.getImageUrl());
        onSuccess = () -> {
            mLastMessageTimestamp = Math.max(msg.getCreatedAt().getTime(), mLastMessageTimestamp);
            mImageUploader.uploadImage(localId, imageUri);
        };
        return onSuccess;
    }

    private void uploadMessage(@NonNull ParseObject msg, @NonNull Action0 onSuccess, @NonNull Action1<Exception> onError) {
        mParseHelper.saveInBackground(msg, e -> {
            if (e == null) {
                onSuccess.call();
            }
            else {
                onError.call(e);
            }
        });
    }

    @VisibleForTesting
    String generateLocalId() {
        return mParseHelper.getCurrentUser().getObjectId() + "-" + System.currentTimeMillis();
    }

    public void pullLatestMessages(String chatId, long newMessageTimestamp) {
        ParseQuery<ParseObject> query = new ParseQuery<>(MessagesTable.NAME);
        query.whereEqualTo(MessagesTable.Fields.CHAT, ParseObject.createWithoutData(ChatTable.NAME, chatId));
        query.addDescendingOrder(MessagesTable.Fields.CREATED_AT);
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ONLY);
        query.include(MessagesTable.Fields.IMAGE);
        query.whereGreaterThanOrEqualTo(ParseUtils.GeneralFields.CREATED_AT, new Date(newMessageTimestamp));
        Log.d(TAG, "Requesting new messages starting at " + newMessageTimestamp);

        mParseHelper.find(query)
            .doOnNext(mSortMessagesAscending)
            .flatMap(Observable::from)
            .map(ParseUtils::baseMessageFromParseObject)
            .doOnNext(getNewMessagePublisher(chatId)::onNext)
            .doOnNext(message -> mLastMessageTimestamp = Math.max(mLastMessageTimestamp, ((BaseMessage) message).getTimestamp()))
            .subscribe();
    }

    public void updateMessage(@NonNull String chatId, @NonNull String messageId) {
        ParseQuery<ParseObject> query = new ParseQuery<>(MessagesTable.NAME);
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ONLY);
        query.include(MessagesTable.Fields.IMAGE);
        Log.d(TAG, "Updating message: " + messageId);

        mParseHelper.get(query, messageId)
            .map(ParseUtils::baseMessageFromParseObject)
            .doOnNext(getUpdatedMessagePublisher(chatId)::onNext)
            .subscribe();
    }

    private PublishSubject<Message> getNewMessagePublisher(String chatId) {
        if (!mNewMessagePublishers.containsKey(chatId)) {
            PublishSubject<Message> publisher = PublishSubject.create();
            mNewMessagePublishers.put(chatId, publisher);
        }
        return mNewMessagePublishers.get(chatId);
    }

    private PublishSubject<Message> getUpdatedMessagePublisher(String chatId) {
        if (!mUpdatedMessagePublishers.containsKey(chatId)) {
            PublishSubject<Message> publisher = PublishSubject.create();
            mUpdatedMessagePublishers.put(chatId, publisher);
        }
        return mUpdatedMessagePublishers.get(chatId);
    }

    public interface ImageUploader {
        /**
         * Upload an image to the server for the given local id and uri.
         */
        void uploadImage(@NonNull String localId, @NonNull Uri uri);
    }
}

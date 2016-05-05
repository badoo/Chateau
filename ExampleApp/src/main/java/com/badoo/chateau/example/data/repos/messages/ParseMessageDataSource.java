package com.badoo.chateau.example.data.repos.messages;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.badoo.chateau.core.repos.messages.MessageDataSource;
import com.badoo.chateau.core.repos.messages.MessageQueries.LoadMessagesQuery;
import com.badoo.chateau.core.repos.messages.MessageQueries.SendMessageQuery;
import com.badoo.chateau.core.repos.messages.MessageQueries.SubscribeToMessagesQuery;
import com.badoo.chateau.data.models.BaseMessage;
import com.badoo.chateau.data.models.payloads.ImagePayload;
import com.badoo.chateau.data.models.payloads.Payload;
import com.badoo.chateau.data.models.payloads.TextPayload;
import com.badoo.chateau.example.data.model.ExampleMessage;
import com.badoo.chateau.example.data.util.ParseHelper;
import com.badoo.chateau.example.data.util.ParseUtils;
import com.badoo.chateau.example.data.util.ParseUtils.ChatTable;
import com.badoo.chateau.example.data.util.ParseUtils.MessagesTable;
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

public class ParseMessageDataSource implements MessageDataSource<ExampleMessage> {

    private static final String TAG = ParseMessageDataSource.class.getSimpleName();
    private final static int MAX_CHUNK_SIZE = 20;

    private final Map<String, PublishSubject<List<ExampleMessage>>> mMessagePublishers = new HashMap<>();

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
    public Observable<Boolean> loadMessages(@NonNull LoadMessagesQuery query) {
        final ParseQuery<ParseObject> parseQuery = new ParseQuery<>(MessagesTable.NAME);
        parseQuery.whereEqualTo(MessagesTable.Fields.CHAT, ParseObject.createWithoutData(ChatTable.NAME, query.getConversationId()));
        parseQuery.addDescendingOrder(MessagesTable.Fields.CREATED_AT);
        if (query.getChunkBefore() != null) {
            parseQuery.whereLessThan(MessagesTable.Fields.CREATED_AT, new Date(((BaseMessage) query.getChunkBefore()).getTimestamp()));
        }
        parseQuery.setLimit(MAX_CHUNK_SIZE);

        parseQuery.include(MessagesTable.Fields.IMAGE);

        return mParseHelper.find(parseQuery)
            .doOnNext(mSortMessagesAscending)
            .flatMap(Observable::from)
            .map(ParseUtils::baseMessageFromParseObject)
            .toList()
            .doOnNext(messages -> publishMessages(query.getConversationId(), messages))
            .map(messages -> messages.size() == MAX_CHUNK_SIZE);
    }

    @Override
    public Observable<Void> sendMessage(@NonNull SendMessageQuery query) {
        final ParseObject msg = ParseObject.create(MessagesTable.NAME);
        final String localId = generateLocalId();

        final String conversationId = query.getConversationId();
        final Action0 onSuccess;
        Payload payload; // Payload used locally until we get a message back from the server
        if (query.getMessage() != null) {
            onSuccess = handleTextPayload(msg, query.getMessage(), conversationId);
            payload = new TextPayload(query.getMessage());
        }
        else if (query.getMediaUri() != null) {
            onSuccess = handleImagePayload(msg, query.getMediaUri(), localId);
            payload = new ImagePayload(query.getMediaUri().toString());
        }
        else {
            throw new IllegalArgumentException("Unsupported message: " + query);
        }

        msg.put(MessagesTable.Fields.CHAT, ParseObject.createWithoutData(ChatTable.NAME, conversationId));
        // Need to ensure the message sent back has the generated local id so it can be matched with responses form the server
        msg.put(MessagesTable.Fields.LOCAL_ID, localId);
        // Give it a timestamp that ensured that it ends up last (this will be replaced by the real server side timestamp)
        mLastMessageTimestamp = Math.max(System.currentTimeMillis(), mLastMessageTimestamp + 1);

        final ExampleMessage unconfirmedMessage = ExampleMessage.createUnconfirmedMessage(localId, mParseHelper.getCurrentUser().getObjectId(), payload, mLastMessageTimestamp);
        getMessagePublisher(conversationId).onNext(Collections.singletonList(unconfirmedMessage));

        uploadMessage(msg, onSuccess, e -> getMessagePublisher(conversationId).onNext(Collections.singletonList(ExampleMessage.createFailedMessage(unconfirmedMessage))));
        return Observable.empty();
    }

    /**
     * Request the latest messages in a conversation to be loaded.
     */
    public void loadLatestMessages(String conversationId, long newMessageTimestamp) {
        ParseQuery<ParseObject> query = new ParseQuery<>(MessagesTable.NAME);
        query.whereEqualTo(MessagesTable.Fields.CHAT, ParseObject.createWithoutData(ChatTable.NAME, conversationId));
        query.addDescendingOrder(MessagesTable.Fields.CREATED_AT);
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ONLY);
        query.include(MessagesTable.Fields.IMAGE);
        query.whereGreaterThanOrEqualTo(ParseUtils.GeneralFields.CREATED_AT, new Date(newMessageTimestamp));
        Log.d(TAG, "Requesting new messages starting at " + newMessageTimestamp);

        mParseHelper.find(query)
            .doOnNext(mSortMessagesAscending)
            .flatMap(Observable::from)
            .map(ParseUtils::baseMessageFromParseObject)
            .doOnNext(message -> mLastMessageTimestamp = Math.max(mLastMessageTimestamp, message.getTimestamp()))
            .toList()
            .subscribe(messages -> {
                getMessagePublisher(conversationId).onNext(messages);
            });
    }

    /**
     * Request a single message in a conversation to be updated.
     */
    public void updateMessage(@NonNull String conversationId, @NonNull String messageId) {
        ParseQuery<ParseObject> query = new ParseQuery<>(MessagesTable.NAME);
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ONLY);
        query.include(MessagesTable.Fields.IMAGE);
        Log.d(TAG, "Updating message: " + messageId);

        mParseHelper.get(query, messageId)
            .map(ParseUtils::baseMessageFromParseObject)
            .toList()
            .subscribe(messages -> {
                getMessagePublisher(conversationId).onNext(messages);
            });
    }

    @NonNull
    @Override
    public Observable<List<ExampleMessage>> subscribeToMessages(@NonNull SubscribeToMessagesQuery<ExampleMessage> query) {
        return getMessagePublisher(query.getConversationId());
    }

    /**
     * Configure the parse object for a text payload an return the action to run on successful sending of the message
     */
    @NonNull
    private Action0 handleTextPayload(@NonNull ParseObject msg, @NonNull String payload, @NonNull String conversationId) {
        Action0 onSuccess;
        msg.put(MessagesTable.Fields.TYPE, MessagesTable.Types.TEXT);
        msg.put(MessagesTable.Fields.MESSAGE, payload);
        onSuccess = () -> {
            mLastMessageTimestamp = Math.max(msg.getCreatedAt().getTime(), mLastMessageTimestamp);
            // Publish the real message back to the listening presenter layer after it is successfully saved (so that it can replace the temporary message)
            getMessagePublisher(conversationId).onNext(Collections.singletonList(ParseUtils.baseMessageFromParseObject(msg)));
        };
        return onSuccess;
    }

    /**
     * Configure the parse object for a image payload an return the action to run on successful sending of the message
     */
    @NonNull
    private Action0 handleImagePayload(@NonNull ParseObject msg, @NonNull Uri payload, @NonNull String localId) {
        Action0 onSuccess;
        msg.put(MessagesTable.Fields.TYPE, MessagesTable.Types.IMAGE);
        onSuccess = () -> {
            mLastMessageTimestamp = Math.max(msg.getCreatedAt().getTime(), mLastMessageTimestamp);
            mImageUploader.uploadImage(localId, payload);
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

    private void publishMessages(String conversationId, List<ExampleMessage> messages) {
        getMessagePublisher(conversationId).onNext(messages);
    }

    private PublishSubject<List<ExampleMessage>> getMessagePublisher(String conversationId) {
        if (!mMessagePublishers.containsKey(conversationId)) {
            PublishSubject<List<ExampleMessage>> publisher = PublishSubject.create();
            mMessagePublishers.put(conversationId, publisher);
        }
        return mMessagePublishers.get(conversationId);
    }

    public interface ImageUploader {
        /**
         * Upload an image to the server for the given local id and uri.
         */
        void uploadImage(@NonNull String localId, @NonNull Uri uri);
    }
}

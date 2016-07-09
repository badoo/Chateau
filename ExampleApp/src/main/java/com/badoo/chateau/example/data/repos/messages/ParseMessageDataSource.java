package com.badoo.chateau.example.data.repos.messages;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.badoo.chateau.core.repos.messages.MessageDataSource;
import com.badoo.chateau.core.repos.messages.MessageQueries;
import com.badoo.chateau.core.repos.messages.MessageQueries.LoadQuery;
import com.badoo.chateau.core.repos.messages.MessageQueries.SendQuery;
import com.badoo.chateau.core.repos.messages.MessageQueries.SubscribeQuery;
import com.badoo.chateau.data.models.payloads.ImagePayload;
import com.badoo.chateau.data.models.payloads.TextPayload;
import com.badoo.chateau.example.Broadcaster;
import com.badoo.chateau.example.data.model.ExampleMessage;
import com.badoo.chateau.example.data.util.ParseHelper;
import com.badoo.chateau.example.data.util.ParseUtils;
import com.badoo.chateau.example.data.util.ParseUtils.ChatTable;
import com.badoo.chateau.example.data.util.ParseUtils.MessagesTable;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.Single;
import rx.SingleSubscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static com.badoo.chateau.core.repos.messages.MessageQueries.LoadQuery.Type;

public class ParseMessageDataSource implements MessageDataSource<ExampleMessage> {

    private static final String TAG = ParseMessageDataSource.class.getSimpleName();
    private final static int MAX_CHUNK_SIZE = 20;

    @NonNull
    private final ImageUploader mImageUploader;
    @NonNull
    private final ParseHelper mParseHelper;
    private final PublishSubject<Update<ExampleMessage>> mUpdatePublisher = PublishSubject.create();

    // In order to guarantee that any new temporary message is inserted as the last entry of the conversation (even if the device
    // time is incorrectly set) we keep track of the timestamp of the last message (across loaded conversations).
    private long mLastMessageTimestamp;

    private Action1<List<ParseObject>> mSortMessagesAscending = parseObjects -> Collections.sort(parseObjects, (lhs, rhs) -> {
        // Sort Acceding.
        final long lhsTimestamp = lhs.getCreatedAt().getTime();
        final long rhsTimestamp = rhs.getCreatedAt().getTime();
        return lhsTimestamp < rhsTimestamp ? -1 : (lhsTimestamp == rhsTimestamp ? 0 : 1);
    });

    public ParseMessageDataSource(@NonNull LocalBroadcastManager broadcastManager, @NonNull ImageUploader imageUploader, @NonNull ParseHelper parseHelper) {
        mImageUploader = imageUploader;
        mParseHelper = parseHelper;

        Broadcaster.ConversationUpdatedReceiver pullLatestMessagesReceiver = new Broadcaster.ConversationUpdatedReceiver() {

            @Override
            public void onConversationUpdated(@NonNull String conversationId, long timestamp) {
                // Should be greater than or equal, -1 fixes this
                loadInternal(conversationId, Type.NEWER, timestamp - 1)
                    .subscribe(result -> {
                        for (ExampleMessage message : result.getMessages()) {
                            mUpdatePublisher.onNext(new Update<>(conversationId, Update.Action.ADDED, null, message));
                        }
                    });
            }

            @Override
            public void onImageUploaded(@NonNull String conversationId, @NonNull String messageId) {
                updateMessage(conversationId, messageId);
            }
        };
        broadcastManager.registerReceiver(pullLatestMessagesReceiver, Broadcaster.getConversationUpdatedFilter());
    }

    @NonNull
    @Override
    public Observable<LoadResult<ExampleMessage>> load(@NonNull LoadQuery<ExampleMessage> query) {
        Log.d(TAG, "Loading: " + query);
        final long timestamp;
        if (query.getType() == Type.NEWER && query.getNewest() != null) {
            timestamp = query.getNewest().getTimestamp();
        }
        else if (query.getType() == Type.OLDER && query.getOldest() != null) {
            timestamp = query.getOldest().getTimestamp();
        }
        else {
            timestamp = 0;
        }
        return Observable.defer(() -> loadInternal(query.getConversationId(), query.getType(), timestamp));
    }

    private Observable<LoadResult<ExampleMessage>> loadInternal(@NonNull String conversationId, @NonNull LoadQuery.Type type, long timestamp) {
        final ParseQuery<ParseObject> parseQuery = new ParseQuery<>(MessagesTable.NAME);
        parseQuery.whereEqualTo(MessagesTable.Fields.CHAT, ParseObject.createWithoutData(ChatTable.NAME, conversationId));
        parseQuery.addDescendingOrder(MessagesTable.Fields.CREATED_AT);
        if (type == Type.OLDER) {
            parseQuery.whereLessThan(MessagesTable.Fields.CREATED_AT, new Date(timestamp));
        }
        else if (type == Type.NEWER) {
            parseQuery.whereGreaterThan(MessagesTable.Fields.CREATED_AT, new Date(timestamp));
        }
        parseQuery.setLimit(MAX_CHUNK_SIZE);
        parseQuery.include(MessagesTable.Fields.IMAGE);

        return mParseHelper.find(parseQuery)
            .doOnNext(mSortMessagesAscending)
            .flatMap(Observable::from)
            .map(in -> ParseUtils.from(in, mParseHelper))
            .toList()
            .map(messages -> {
                final boolean canLoadOlder;
                final boolean canLoadNewer;
                if (type == Type.OLDER) {
                    canLoadOlder = !messages.isEmpty();
                    canLoadNewer = true; // This will be ignored in any case since we are loading older messages
                }
                else if (type == Type.NEWER) {
                    canLoadOlder = true; // This will be ignored in any case since we are loading newer messages
                    canLoadNewer = !messages.isEmpty();
                }
                else {
                    canLoadOlder = !messages.isEmpty();
                    canLoadNewer = !messages.isEmpty();
                }
                return new LoadResult<>(messages, canLoadOlder, canLoadNewer);
            })
            .subscribeOn(Schedulers.io());
    }

    @NonNull
    @Override
    public Observable<Void> send(@NonNull SendQuery<ExampleMessage> query) {
        final ExampleMessage message = query.getMessage();
        final String conversationId = query.getConversationId();
        final ParseObject parseMessage = createOutgoingParseObject(conversationId, message);
        // Give it a timestamp that ensured that it ends up last (this will be replaced by the real server side timestamp)
        mLastMessageTimestamp = Math.max(System.currentTimeMillis(), mLastMessageTimestamp + 1);

        final ExampleMessage unconfirmedMessage = ExampleMessage.createUnconfirmedMessage(message.getLocalId(), mParseHelper.getCurrentUser().getObjectId(), message.getPayload(), mLastMessageTimestamp);

        mUpdatePublisher.onNext(new Update<>(conversationId, Update.Action.ADDED, null, unconfirmedMessage));

        return saveMessage(parseMessage)
            .observeOn(Schedulers.io())
            .doOnError(throwable -> onFailedToSend(conversationId, unconfirmedMessage))
            .doOnSuccess(parseObject -> onMessageSent(conversationId, parseObject, unconfirmedMessage))
            .toObservable()
            .ignoreElements()
            .cast(Void.class);
    }

    private ParseObject createOutgoingParseObject(String conversationId, ExampleMessage message) {
        final ParseObject parseMessage = ParseObject.create(MessagesTable.NAME);
        parseMessage.put(MessagesTable.Fields.FROM, mParseHelper.getCurrentUser());
        if (message.getPayload() instanceof TextPayload) {
            parseMessage.put(MessagesTable.Fields.TYPE, MessagesTable.Types.TEXT);
            parseMessage.put(MessagesTable.Fields.MESSAGE, ((TextPayload) message.getPayload()).getMessage());
        }
        else if (message.getPayload() instanceof ImagePayload) {
            parseMessage.put(MessagesTable.Fields.TYPE, MessagesTable.Types.IMAGE);
        }

        parseMessage.put(MessagesTable.Fields.CHAT, ParseObject.createWithoutData(ChatTable.NAME, conversationId));
        // Need to ensure the message sent back has the generated local id so it can be matched with responses form the server
        parseMessage.put(MessagesTable.Fields.LOCAL_ID, message.getLocalId());
        return parseMessage;
    }

    private void onFailedToSend(String conversationId, ExampleMessage unconfirmedMessage) {
        mUpdatePublisher.onNext(new Update<>(conversationId, Update.Action.UPDATED, unconfirmedMessage, ExampleMessage.createFailedMessage(unconfirmedMessage)));
    }

    private void onMessageSent(String conversationId, ParseObject parseMessage, ExampleMessage message) {
        if (message.getPayload() instanceof TextPayload) {
            notifyTextMessageSent(parseMessage, conversationId);
        }
        else if (message.getPayload() instanceof ImagePayload) {
            notifyPhotoMessageSent(parseMessage, Uri.parse(((ImagePayload) message.getPayload()).getImageUrl()), message.getLocalId());
        }
        else {
            throw new IllegalArgumentException("Unsupported message: " + message);
        }
    }

    /**
     * Request a single message in a conversation to be updated.
     */
    private void updateMessage(@NonNull String conversationId, @NonNull String messageId) {
        ParseQuery<ParseObject> query = new ParseQuery<>(MessagesTable.NAME);
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ONLY);
        query.include(MessagesTable.Fields.IMAGE);
        Log.d(TAG, "Updating message: " + messageId);

        mParseHelper.get(query, messageId)
            .map(in -> ParseUtils.from(in, mParseHelper))
            .toList()
            .subscribe(messages -> {
                for (ExampleMessage message : messages) {
                    mUpdatePublisher.onNext(new Update<>(conversationId, Update.Action.UPDATED, null, message));
                }
            });
    }

    @NonNull
    @Override
    public Observable<List<ExampleMessage>> getUndelivered(@NonNull MessageQueries.GetUndeliveredQuery<ExampleMessage> query) {
        throw new UnsupportedOperationException("Not implemented for this provider");
    }

    @NonNull
    @Override
    public Observable<Update<ExampleMessage>> subscribe(@NonNull SubscribeQuery<ExampleMessage> query) {
        return mUpdatePublisher;
    }

    private void notifyTextMessageSent(@NonNull ParseObject msg, @NonNull String conversationId) {
        final long messageTimestamp = msg.getCreatedAt() != null ? msg.getCreatedAt().getTime() : 0;
        mLastMessageTimestamp = Math.max(messageTimestamp, mLastMessageTimestamp);
        // Publish the real message back to the listening presenter layer after it is successfully saved (so that it can replace the temporary message)
        mUpdatePublisher.onNext(new Update<>(conversationId, Update.Action.UPDATED, null, ParseUtils.from(msg, mParseHelper)));
    }

    private void notifyPhotoMessageSent(@NonNull ParseObject msg, @NonNull Uri payload, @NonNull String localId) {
        mLastMessageTimestamp = Math.max(msg.getCreatedAt().getTime(), mLastMessageTimestamp);
        mImageUploader.uploadImage(localId, payload);
    }

    private Single<ParseObject> saveMessage(@NonNull ParseObject message) {
        return Single.create(new Single.OnSubscribe<ParseObject>() {
            @Override
            public void call(SingleSubscriber<? super ParseObject> subscriber) {
                mParseHelper.save(message);
                if (subscriber.isUnsubscribed()) {
                    return;
                }
                subscriber.onSuccess(message);
            }
        });
    }

    public interface ImageUploader {
        /**
         * Upload an image to the server for the given local id and uri.
         */
        void uploadImage(@NonNull String localId, @NonNull Uri uri);
    }
}

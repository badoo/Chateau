package com.badoo.chateau;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Class containing methods for creating common broadcasts to notify about changes in the application or data state.
 */
public class Broadcaster {

    private static final String ACTION_CONVERSATION_UPDATED = Broadcaster.class.getSimpleName() + "_action_conversationUpdated";
    private static final String ACTION_OTHER_USER_TYPING = Broadcaster.class.getSimpleName() + "_action_otherUserTyping";
    private static final String ACTION_IMAGE_UPLOADED = Broadcaster.class.getSimpleName() + "_action_imageUploaded";
    private static final String ACTION_USER_SIGNED_IN = Broadcaster.class.getSimpleName() + "_action_userSignedIn";
    private static final String ACTION_USER_SIGNED_OUT = Broadcaster.class.getSimpleName() + "_action_userSignedOut";

    private static final String ARG_CHAT_ID = Broadcaster.class.getSimpleName() + "_arg_chatId";
    private static final String ARG_USER_ID = Broadcaster.class.getSimpleName() + "_arg_userId";
    private static final String ARG_TIMESTAMP = Broadcaster.class.getSimpleName() + "_arg_timestamp";
    private static final String ARG_MESSAGE_ID = Broadcaster.class.getSimpleName() + "_arg_messageId";

    public static IntentFilter getConversationUpdatedFilter() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_CONVERSATION_UPDATED);
        filter.addAction(ACTION_IMAGE_UPLOADED);
        return filter;
    }

    public static IntentFilter getOtherUserTypingFilter() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_OTHER_USER_TYPING);
        return filter;
    }

    public static IntentFilter getUserSignInStateChangedFilter() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USER_SIGNED_IN);
        filter.addAction(ACTION_USER_SIGNED_OUT);
        return filter;
    }

    private Context mContext;

    public Broadcaster(@NonNull Context context) {
        mContext = context;
    }

    /**
     * Sends a broadcast notifying that the user has signed in.
     */
    public void userSignedIn() {
        Intent intent = new Intent(ACTION_USER_SIGNED_IN);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    /**
     * Sends a broadcast notifying that the user has signed out.
     */
    public void userSignedOut() {
        Intent intent = new Intent(ACTION_USER_SIGNED_OUT);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    /**
     * Sends a broadcast to notify that an image finished uploading (in one of the conversations that the user is taking part in)
     */
    public void imageUploaded(@NonNull String chatId, @NonNull String messageId) {
        Intent intent = new Intent(ACTION_IMAGE_UPLOADED);
        intent.putExtra(ARG_CHAT_ID, chatId);
        intent.putExtra(ARG_MESSAGE_ID, messageId);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    /**
     * Sends a broadcast to notify that another user is typing in a conversation
     */
    public void otherUserTyping(@NonNull String userId, @NonNull String chatId) {
        Intent intent = new Intent(ACTION_OTHER_USER_TYPING);
        intent.putExtra(ARG_USER_ID, userId);
        intent.putExtra(ARG_CHAT_ID, chatId);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    /**
     * Sends a broadcast notifying that a conversation has been updated.
     */
    public void conversationUpdated(@NonNull String chatId, long timestamp) {
        Intent intent = new Intent(ACTION_CONVERSATION_UPDATED);
        intent.putExtra(ARG_CHAT_ID, chatId);
        intent.putExtra(ARG_TIMESTAMP, timestamp);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    public abstract static class ConversationUpdatedReceiver extends BroadcastReceiver {

        @Override
        public final void onReceive(Context context, Intent intent) {
            if (ACTION_CONVERSATION_UPDATED.equals(intent.getAction())) {
                onConversationUpdated(intent.getStringExtra(ARG_CHAT_ID), intent.getLongExtra(ARG_TIMESTAMP, 0));
            }
            else if (ACTION_IMAGE_UPLOADED.equals(intent.getAction())) {
                onImageUploaded(intent.getStringExtra(ARG_CHAT_ID), intent.getStringExtra(ARG_MESSAGE_ID));
            }
        }

        public abstract void onConversationUpdated(@NonNull String chatId, long timestamp);

        protected abstract void onImageUploaded(@NonNull String chatId, @NonNull String messageId);
    }

    public abstract static class OtherUserTypingReceiver extends BroadcastReceiver {

        @Override
        public final void onReceive(Context context, Intent intent) {
            if (ACTION_OTHER_USER_TYPING.equals(intent.getAction())) {
                onOtherUserTyping(intent.getStringExtra(ARG_USER_ID), intent.getStringExtra(ARG_CHAT_ID));
            }
        }

        protected abstract void onOtherUserTyping(@NonNull String userId, @NonNull String chatId);

    }

    public abstract static class UserSignInStateChangedReceiver extends BroadcastReceiver {

        @Override
        public final void onReceive(Context context, Intent intent) {
            if (ACTION_USER_SIGNED_IN.equals(intent.getAction())) {
                onUserSignedIn();
            }
            else if (ACTION_USER_SIGNED_OUT.equals(intent.getAction())) {
                onUserSignedOut();
            }
        }

        protected abstract void onUserSignedIn();

        protected abstract void onUserSignedOut();
    }
}

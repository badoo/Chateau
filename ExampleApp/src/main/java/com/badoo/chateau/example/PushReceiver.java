package com.badoo.chateau.example;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.badoo.chateau.example.R;
import com.badoo.chateau.example.ui.chat.ChatActivity;
import com.badoo.chateau.example.ui.conversations.list.ConversationListActivity;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

public class PushReceiver extends ParsePushBroadcastReceiver {

    private static final String TAG = PushReceiver.class.getSimpleName();

    private static final String NEW_MESSAGE_IN_CHAT_PUSH = "newMessageInChat";


    public PushReceiver() {
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        final String payload = intent.getStringExtra(KEY_PUSH_DATA);
        Log.d(TAG, "Push payload: " + payload);
        try {
            JSONObject json = new JSONObject(payload);
            String type = json.getString("type");
            JSONObject data = json.getJSONObject("data");
            handlePush(context, type, data);
        }
        catch (JSONException e) {
            Log.e(TAG, "Failed to parse payload", e);
        }
    }

    private void handlePush(@NonNull Context context, @NonNull String type, @NonNull JSONObject data) throws JSONException {
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (NEW_MESSAGE_IN_CHAT_PUSH.equals(type)) {
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(data.getString("name"))
                .setSmallIcon(R.drawable.ic_notification_new_message)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                .setContentText(data.getString("message"))
                .setAutoCancel(true);

            builder.setContentIntent(createPendingIntent(context, data.getString("chatId")));
            notificationManager.notify(data.getString("userId").hashCode(), builder.build());
        }
    }

    private PendingIntent createPendingIntent(@NonNull Context context, @NonNull String chatId) {
        final TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        final Intent conversations = new Intent(context, ConversationListActivity.class);
        stackBuilder.addNextIntent(conversations);
        final Intent chat = ChatActivity.create(context, chatId, "");
        stackBuilder.addNextIntent(chat);
        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);
    }
}

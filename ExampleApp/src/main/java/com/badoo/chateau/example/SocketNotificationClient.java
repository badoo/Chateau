package com.badoo.chateau.example;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Client for connecting to the socket notification proxy server which send notifications about new messages.
 */
public class SocketNotificationClient {

    private static final String TAG = SocketNotificationClient.class.getSimpleName();
    private static final String TYPE_NEW_MESSAGE = "newMessageInChat";
    private static final String TYPE_IMAGE_UPLOADED = "imageUploaded";
    private static final String TYPE_USER_TYPING = "userTyping";
    private static final long RETRY_TIMEOUT = 10 * 1000;

    @NonNull
    private final Handler mRetryHandler = new Handler(Looper.getMainLooper());
    @NonNull
    private final String mHost;
    private final int mPort;
    @NonNull
    private final Broadcaster mBroadcaster;
    @Nullable
    private SocketThread mThread;
    boolean mRestartOnDisconnect = false;

    public SocketNotificationClient(@NonNull Broadcaster broadcaster, @NonNull String host, int port) {
        mBroadcaster = broadcaster;
        mHost = host;
        mPort = port;
    }

    /**
     * Starts and connects the client. After this call any received socket push will be broadcast as a local broadcast.
     */
    @MainThread
    public void start() {
        mRestartOnDisconnect = true;
        if (mThread != null) {
            return;
        }
        Log.d(TAG, "Starting socket notification client");
        mThread = new SocketThread();
        mThread.start();
    }

    /**
     * Stops and disconnects the client.
     */
    @MainThread
    public void stop() {
        mRestartOnDisconnect = false;
        // Close the connection
        Log.d(TAG, "Stopping socket notification client");
        if (mThread != null) {
            mThread.tryToStop();
        }
        cleanup();
    }

    /**
     * Restarts the client, making sure that the registration is updated.
     */
    public void restart() {
        stop();
        start();
    }

    private void cleanup() {
        mThread = null;
    }

    private void scheduleRestart() {
        if (mRestartOnDisconnect) {
            mRetryHandler.postDelayed(this::start, RETRY_TIMEOUT);
        }
    }

    private void onNotification(JSONObject payload) throws JSONException {
        final String type = payload.getString("type");
        if (TYPE_NEW_MESSAGE.equals(type)) {
            final String chatId = payload.getString("chatId");
            final long timestamp = payload.getLong("timestamp");
            mBroadcaster.conversationUpdated(chatId, timestamp);
        }
        else if (TYPE_IMAGE_UPLOADED.equals(type)) {
            final String chatId = payload.getString("chatId");
            final String messageId = payload.getString("messageId");
            mBroadcaster.imageUploaded(chatId, messageId);
        }
        else if (TYPE_USER_TYPING.equals(type)) {
            final String chatId = payload.getString("chatId");
            final String userId = payload.getString("userId");
            mBroadcaster.otherUserTyping(userId, chatId);
        }
    }

    private class SocketThread extends Thread {

        private OutputStreamWriter mOut;
        private BufferedReader mIn;
        @Nullable
        private Socket mSocket;

        public SocketThread() {
            super("SocketThread");
        }

        public void tryToStop() {
            interrupt();
            if (mSocket != null) {
                try {
                    mSocket.close();
                }
                catch (IOException e) {
                    //Ignored
                }
            }
        }

        @Override
        public void run() {
            try {
                mSocket = new Socket(mHost, mPort);
                mOut = new OutputStreamWriter(mSocket.getOutputStream());
                mIn = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                Log.d(TAG, "Connected to socket notification server");
                // Send subscription request
                if (ParseUser.getCurrentUser() != null) {
                    final String id = ParseUser.getCurrentUser().getObjectId();
                    mOut.write("{\"type\":\"register\", \"value\":\"" + id + "\"}");
                    mOut.flush();
                }
                // Start listening for notifications
                while (true) {
                    final String data = mIn.readLine();
                    if (data == null) {
                        throw new IOException("Socket dropped?");
                    }
                    JSONObject json = new JSONObject(data);
                    onNotification(json);
                    Log.d(TAG, "Received data from notification server: " + data);
                }
            }
            catch (Exception e) {
                Log.e(TAG, "Exception on socket thread", e);
                cleanup();
                scheduleRestart();
            }
            Log.d(TAG, "Socket notification thread stopping");
        }
    }

}

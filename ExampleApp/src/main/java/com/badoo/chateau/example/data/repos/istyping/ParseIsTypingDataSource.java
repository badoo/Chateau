package com.badoo.chateau.example.data.repos.istyping;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import com.badoo.chateau.Broadcaster;
import com.badoo.chateau.example.data.util.ParseUtils;
import com.badoo.chateau.core.model.User;
import com.badoo.chateau.core.repos.istyping.IsTypingDataSource;
import com.badoo.chateau.core.repos.istyping.IsTypingQuery;
import com.parse.ParseCloud;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.subjects.PublishSubject;

public class ParseIsTypingDataSource implements IsTypingDataSource {

    private final Map<String, PublishSubject<User>> mPublishers = new HashMap<>();

    public ParseIsTypingDataSource(Context context) {
        LocalBroadcastManager.getInstance(context).registerReceiver(new Broadcaster.OtherUserTypingReceiver() {
            @Override
            protected void onOtherUserTyping(@NonNull String userId, @NonNull String chatId) {
                // TODO Pass back some useful data which we can use for group chats
                PublishSubject<User> publisher = mPublishers.get(chatId);
                if (publisher != null) {
                    publisher.onNext(new User() {
                    });
                }
            }
        }, Broadcaster.getOtherUserTypingFilter());
    }

    @Override
    public void sendUserIsTyping(@NonNull IsTypingQuery.SendIsTyping query) {
        final Map<String, Object> params = new HashMap<>();
        params.put(ParseUtils.SendUserTypingFunc.Fields.CHAT_ID, query.getChatId());
        ParseCloud.callFunctionInBackground(ParseUtils.SendUserTypingFunc.NAME, params);
    }

    @NonNull
    @Override
    public Observable<User> SubscribeToUsersTyping(@NonNull IsTypingQuery.SubscribeToUsersTyping query) {
        if (!mPublishers.containsKey(query.getChatId())) {
            PublishSubject<User> publisher = PublishSubject.create();
            mPublishers.put(query.getChatId(), publisher);
        }
        return mPublishers.get(query.getChatId());
    }
}

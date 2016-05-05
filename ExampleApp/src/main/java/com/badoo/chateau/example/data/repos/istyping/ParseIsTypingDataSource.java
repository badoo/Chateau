package com.badoo.chateau.example.data.repos.istyping;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import com.badoo.chateau.core.repos.istyping.IsTypingDataSource;
import com.badoo.chateau.core.repos.istyping.IsTypingQueries;
import com.badoo.chateau.example.Broadcaster;
import com.badoo.chateau.example.data.model.ExampleUser;
import com.badoo.chateau.example.data.util.ParseHelper;
import com.badoo.chateau.example.data.util.ParseUtils;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.subjects.PublishSubject;

public class ParseIsTypingDataSource implements IsTypingDataSource<ExampleUser> {

    private final Map<String, PublishSubject<ExampleUser>> mPublishers = new HashMap<>();
    private final ParseHelper mParseHelper;

    public ParseIsTypingDataSource(@NonNull Context context, @NonNull ParseHelper parseHelper) {
        mParseHelper = parseHelper;
        LocalBroadcastManager.getInstance(context).registerReceiver(new Broadcaster.OtherUserTypingReceiver() {
            @Override
            protected void onOtherUserTyping(@NonNull String userId, @NonNull String chatId) {
                // TODO Pass back some useful data which we can use for group chats
                PublishSubject<ExampleUser> publisher = mPublishers.get(chatId);
                if (publisher != null) {
                    publisher.onNext(new ExampleUser(userId, null));
                }
            }
        }, Broadcaster.getOtherUserTypingFilter());
    }

    @Override
    public Observable<Void> sendUserIsTyping(@NonNull IsTypingQueries.SendIsTyping query) {
        final Map<String, Object> params = new HashMap<>();
        params.put(ParseUtils.SendUserTypingFunc.Fields.CHAT_ID, query.getConversationId());
        return mParseHelper.callFunction(ParseUtils.SendUserTypingFunc.NAME, params);
    }

    @NonNull
    @Override
    public Observable<ExampleUser> subscribeToUsersTyping(@NonNull IsTypingQueries.SubscribeToUsersTypingQuery query) {
        if (!mPublishers.containsKey(query.getConversationId())) {
            PublishSubject<ExampleUser> publisher = PublishSubject.create();
            mPublishers.put(query.getConversationId(), publisher);
        }
        return mPublishers.get(query.getConversationId());
    }
}

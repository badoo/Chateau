package com.badoo.chateau.example.data.repos.conversations;

import android.support.annotation.NonNull;
import android.util.Log;

import com.badoo.chateau.core.repos.conversations.ConversationDataSource;
import com.badoo.chateau.core.repos.conversations.ConversationQueries.CreateConversationQuery;
import com.badoo.chateau.core.repos.conversations.ConversationQueries.CreateGroupConversationQuery;
import com.badoo.chateau.core.repos.conversations.ConversationQueries.DeleteConversationsQuery;
import com.badoo.chateau.core.repos.conversations.ConversationQueries.GetConversationQuery;
import com.badoo.chateau.core.repos.conversations.ConversationQueries.LoadConversationsQuery;
import com.badoo.chateau.core.repos.conversations.ConversationQueries.LoadConversationsQuery.Type;
import com.badoo.chateau.core.repos.conversations.ConversationQueries.MarkConversationReadQuery;
import com.badoo.chateau.core.repos.conversations.ConversationQueries.SubscribeToUpdatesQuery;
import com.badoo.chateau.example.data.model.ExampleConversation;
import com.badoo.chateau.example.data.util.ParseHelper;
import com.badoo.chateau.example.data.util.ParseUtils;
import com.badoo.chateau.example.data.util.ParseUtils.ChatSubscriptionTable;
import com.badoo.chateau.example.data.util.ParseUtils.ChatTable;
import com.badoo.chateau.example.data.util.ParseUtils.CreateChatFunc;
import com.badoo.chateau.example.data.util.ParseUtils.DeleteConversationsFunc;
import com.badoo.chateau.example.data.util.ParseUtils.GetMySubscriptionsFunc;
import com.badoo.chateau.example.data.util.ParseUtils.MarkChatReadFunc;
import com.badoo.chateau.example.data.util.ParseUtils.UsersTable;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class ParseConversationDataSource implements ConversationDataSource<ExampleConversation> {

    private static final String TAG = ParseConversationDataSource.class.getSimpleName();

    private final PublishSubject<Boolean> mConversationUpdatePublisher = PublishSubject.create();
    private final ParseHelper mParseHelper;

    public ParseConversationDataSource(@NonNull ParseHelper parseHelper) {
        mParseHelper = parseHelper;
    }

    public void reloadConversation(@NonNull String chatId) {
        Log.d(TAG, "Conversation updated: " + chatId);
        load(new LoadConversationsQuery<>(Type.ALL)).subscribe();
    }

    @NonNull
    @Override
    public Observable<LoadResult<ExampleConversation>> load(LoadConversationsQuery<ExampleConversation> query) {
        return mParseHelper.<List<ParseObject>>callFunction(GetMySubscriptionsFunc.NAME, Collections.emptyMap())
            .flatMap(Observable::from)
            .map(in -> ParseUtils.conversationFromSubscription(in, mParseHelper))
            .toList()
            .map(c -> new LoadResult<>(c, false, false))
            .subscribeOn(Schedulers.io());
    }

    @NonNull
    @Override
    public Observable<ExampleConversation> get(GetConversationQuery query) {
        return getConversation(query.getConversationId());
    }

    @NonNull
    @Override
    public Observable<Boolean> subscribe(SubscribeToUpdatesQuery query) {
        return mConversationUpdatePublisher;
    }

    @NonNull
    @Override
    public Observable<ExampleConversation> create(CreateGroupConversationQuery query) {
        final Map<String, Object> params = new HashMap<>();
        params.put(CreateChatFunc.Fields.OTHER_USER_IDS, query.getUserIds());
        params.put(CreateChatFunc.Fields.GROUP_NAME, query.getName());

        return mParseHelper.<ParseObject>callFunction(CreateChatFunc.NAME, params)
            .map(in -> ParseUtils.conversationFromChat(in, mParseHelper))
            .subscribeOn(Schedulers.io());
    }

    @NonNull
    @Override
    public Observable<ExampleConversation> create(CreateConversationQuery query) {
        final List<String> userIds = new ArrayList<>();
        userIds.add(query.getUserId());
        final Map<String, Object> params = new HashMap<>();
        params.put(CreateChatFunc.Fields.OTHER_USER_IDS, userIds);

        return mParseHelper.<ParseObject>callFunction(CreateChatFunc.NAME, params)
            .map(in -> ParseUtils.conversationFromChat(in, mParseHelper))
            .subscribeOn(Schedulers.io());
    }

    @NonNull
    @Override
    public Observable<Void> markRead(MarkConversationReadQuery query) {
        final Map<String, Object> params = new HashMap<>();
        params.put(MarkChatReadFunc.Fields.CHAT_ID, query.getConversationId());
        return mParseHelper.<ParseObject>callFunction(MarkChatReadFunc.NAME, params)
            .ignoreElements()
            .cast(Void.class)
            .subscribeOn(Schedulers.io());
    }

    @NonNull
    @Override
    public Observable<List<ExampleConversation>> delete(DeleteConversationsQuery<ExampleConversation> query) {
        final Map<String, Object> params = new HashMap<>();
        List<String> chatIds = new ArrayList<>();
        for (ExampleConversation c : query.getConversations()) {
            chatIds.add(c.getId());
        }
        params.put(DeleteConversationsFunc.Fields.CHAT_IDS, chatIds);
        // The response contains all deleted conversations
        return mParseHelper.<List<ParseObject>>callFunction(DeleteConversationsFunc.NAME, params)
            .flatMap(Observable::from)
            .map(in -> ParseUtils.conversationFromChat(in, mParseHelper))
            .doOnNext(exampleConversation -> mConversationUpdatePublisher.onNext(true))
            .toList()
            .ignoreElements()
            .concatWith(load(null).ignoreElements().map(i -> Collections.<ExampleConversation>emptyList()))
            .subscribeOn(Schedulers.io());
    }

    private Observable<ExampleConversation> getConversation(@NonNull String chatId) {
        final ParseQuery<ParseObject> parseQuery = new ParseQuery<>(ChatSubscriptionTable.NAME);
        parseQuery.include(ChatSubscriptionTable.Fields.CHAT);
        parseQuery.include(ChatSubscriptionTable.Fields.CHAT + "." + ChatTable.Fields.LAST_MESSAGE);
        parseQuery.whereEqualTo(ChatSubscriptionTable.Fields.CHAT, ParseObject.createWithoutData(ChatTable.NAME, chatId));
        parseQuery.whereEqualTo(ChatSubscriptionTable.Fields.USER, ParseObject.createWithoutData(UsersTable.NAME, mParseHelper.getCurrentUser().getObjectId()));
        return mParseHelper.find(parseQuery)
            .flatMap(Observable::from)
            .map(in -> ParseUtils.conversationFromSubscription(in, mParseHelper))
            .subscribeOn(Schedulers.io());
    }
}

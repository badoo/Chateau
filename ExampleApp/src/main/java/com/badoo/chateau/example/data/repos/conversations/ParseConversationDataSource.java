package com.badoo.chateau.example.data.repos.conversations;

import android.support.annotation.NonNull;
import android.util.Log;

import com.badoo.chateau.core.model.Conversation;
import com.badoo.chateau.core.repos.conversations.ConversationDataSource;
import com.badoo.chateau.core.repos.conversations.ConversationQuery;
import com.badoo.chateau.data.models.BaseConversation;
import com.badoo.chateau.data.models.BaseUser;
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
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class ParseConversationDataSource implements ConversationDataSource {

    private static final String TAG = ParseConversationDataSource.class.getSimpleName();

    private final PublishSubject<Conversation> mConversationUpdatePublisher = PublishSubject.create();
    private final ParseHelper mParseHelper;

    public ParseConversationDataSource(@NonNull ParseHelper parseHelper) {
        mParseHelper = parseHelper;
    }

    public void reloadConversation(@NonNull String chatId) {
        Log.d(TAG, "Conversation updated: " + chatId);
        getConversation(chatId).subscribe(mConversationUpdatePublisher::onNext);
    }

    @NonNull
    @Override
    public Observable<Conversation> getConversationsForLoggedInUser(ConversationQuery.GetConversationsForCurrentUserQuery query) {
        return mParseHelper.<List<ParseObject>>callFunction(GetMySubscriptionsFunc.NAME, Collections.emptyMap())
            .flatMap(Observable::from)
            .map(ParseUtils::conversationFromSubscription)
            .subscribeOn(Schedulers.io());
    }

    @NonNull
    @Override
    public Observable<Conversation> getConversation(ConversationQuery.GetConversationQuery query) {
        return getConversation(query.getChatId());
    }

    @NonNull
    @Override
    public Observable<Conversation> subscribeToUpdates(ConversationQuery.SubscribeToConversationUpdatesQuery query) {
        return mConversationUpdatePublisher;
    }

    @NonNull
    @Override
    public Observable<Conversation> createGroupConversation(ConversationQuery.CreateGroupConversationQuery query) {
        final Map<String, Object> params = new HashMap<>();
        params.put(CreateChatFunc.Fields.OTHER_USER_IDS, query.getUserIds());
        params.put(CreateChatFunc.Fields.GROUP_NAME, query.getName());

        return mParseHelper.<ParseObject>callFunction(CreateChatFunc.NAME, params)
            .map(ParseUtils::conversationFromChat)
            .subscribeOn(Schedulers.io());
    }

    @NonNull
    @Override
    public Observable<Conversation> createConversation(ConversationQuery.CreateConversationQuery query) {
        final List<String> userIds = new ArrayList<>();
        userIds.add(((BaseUser) query.getUser()).getUserId());
        final Map<String, Object> params = new HashMap<>();
        params.put(CreateChatFunc.Fields.OTHER_USER_IDS, userIds);

        return mParseHelper.<ParseObject>callFunction(CreateChatFunc.NAME, params)
            .map(ParseUtils::conversationFromChat)
            .subscribeOn(Schedulers.io());
    }

    @NonNull
    @Override
    public Observable<Void> markConversationRead(ConversationQuery.MarkConversationReadQuery query) {
        final Map<String, Object> params = new HashMap<>();
        params.put(MarkChatReadFunc.Fields.CHAT_ID, query.getChatId());
        return mParseHelper.<ParseObject>callFunction(MarkChatReadFunc.NAME, params)
            .map(__ -> null)
            .cast(Void.class)
            .subscribeOn(Schedulers.io());
    }

    @NonNull
    @Override
    public Observable<Conversation> deleteConversations(ConversationQuery.DeleteConversationsQuery query) {
        final Map<String, Object> params = new HashMap<>();
        List<String> chatIds = new ArrayList<>();
        for (Conversation c : query.getConversations()) {
            chatIds.add(((BaseConversation) c).getId());
        }
        params.put(DeleteConversationsFunc.Fields.CHAT_IDS, chatIds);
        // The response contains all deleted conversations
        return mParseHelper.<List<ParseObject>>callFunction(DeleteConversationsFunc.NAME, params)
            .flatMap(Observable::from)
            .map(ParseUtils::conversationFromSubscription)
            .subscribeOn(Schedulers.io());
    }

    private Observable<Conversation> getConversation(@NonNull String chatId) {
        final ParseQuery<ParseObject> parseQuery = new ParseQuery<>(ChatSubscriptionTable.NAME);
        parseQuery.include(ChatSubscriptionTable.Fields.CHAT);
        parseQuery.include(ChatSubscriptionTable.Fields.CHAT + "." + ChatTable.Fields.LAST_MESSAGE);
        parseQuery.whereEqualTo(ChatSubscriptionTable.Fields.CHAT, ParseObject.createWithoutData(ChatTable.NAME, chatId));
        parseQuery.whereEqualTo(ChatSubscriptionTable.Fields.USER, ParseObject.createWithoutData(UsersTable.NAME, mParseHelper.getCurrentUser().getObjectId()));
        return mParseHelper.find(parseQuery)
            .flatMap(Observable::from)
            .map(ParseUtils::conversationFromSubscription)
            .subscribeOn(Schedulers.io());
    }
}

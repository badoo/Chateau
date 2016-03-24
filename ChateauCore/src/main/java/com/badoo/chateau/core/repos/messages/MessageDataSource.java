package com.badoo.chateau.core.repos.messages;

import android.support.annotation.NonNull;

import com.badoo.barf.data.repo.annotations.Handles;
import com.badoo.chateau.core.model.Message;
import com.badoo.chateau.core.repos.messages.MessageQuery.GetMessages;
import com.badoo.chateau.core.repos.messages.MessageQuery.SubscribeToNewMessagesForConversation;
import com.badoo.chateau.core.repos.messages.MessageQuery.SendMessage;

import rx.Observable;


public interface MessageDataSource {

    /**
     * Returns an {@link Observable} which emits multiple messages. Depending on if message loading is paged it might only return a subset
     * of all messages of a conversation.
     */
    @NonNull
    @Handles(GetMessages.class)
    Observable<Message> getMessages(@NonNull GetMessages query);

    @Handles(SendMessage.class)
    void sendMessage(@NonNull SendMessage query);

    /**
     * Subscribe to new messages posted to a conversation.
     */
    @NonNull
    @Handles(SubscribeToNewMessagesForConversation.class)
    Observable<Message> subscribeToNewMessage(@NonNull SubscribeToNewMessagesForConversation query);

    @NonNull
    @Handles(MessageQuery.GetUpdatedMessagesForConversation.class)
    Observable<Message> subscribeToUpdatedMessage(@NonNull MessageQuery.GetUpdatedMessagesForConversation query);
}

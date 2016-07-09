package com.badoo.chateau.example.data.util;

import android.text.TextUtils;

import com.badoo.chateau.data.models.BaseUser;
import com.badoo.chateau.data.models.payloads.ImagePayload;
import com.badoo.chateau.data.models.payloads.Payload;
import com.badoo.chateau.data.models.payloads.TextPayload;
import com.badoo.chateau.example.data.model.ExampleConversation;
import com.badoo.chateau.example.data.model.ExampleMessage;
import com.badoo.chateau.example.data.model.ExampleUser;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Collections;
import java.util.List;

public class ParseUtils {

    /***************
     * Parse Tables
     ***************/

    public interface UsersTable {
        String NAME = "_User";

        interface Fields extends GeneralFields {
            String DISPLAY_NAME = "displayName";
        }
    }

    public interface ChatSubscriptionTable {
        String NAME = "ChatSubscription";

        interface Fields extends GeneralFields {
            String NAME = "name";
            String CHAT = "chat";
            String USER = "user";
            String LAST_SEEN_COUNT = "lastSeenCount";
        }
    }

    public interface ChatTable extends GeneralFields {
        String NAME = "Chat";

        interface Fields {
            String NAME = "name";
            String LAST_MESSAGE = "lastMessage";
            String MESSAGE_COUNT = "messageCount";
        }
    }

    public interface MessagesTable {
        String NAME = "Message";

        interface Fields extends GeneralFields {
            String LOCAL_ID = "localId";
            String CHAT = "chat";
            String FROM = "from";
            String MESSAGE = "message";
            String IMAGE = "image";
            String TYPE = "type";
        }

        interface Types {

            String TEXT = "text";
            String IMAGE = "image";

        }
    }

    public interface ImagesTable {

        String NAME = "Image";
        String PLACEHOLDER_ID = "10Vi33xiU0";

        interface Fields extends GeneralFields {
            String LOCAL_MESSAGE_ID = "localMessageId";
            String IMAGE = "image";
            String THUMBNAIL = "thumbnail";
        }
    }

    public interface GeneralFields {
        String CREATED_AT = "createdAt";
        String UPDATED_AT = "updatedAt";
    }

    /******************
     * Parse Functions
     ******************/

    public interface CreateChatFunc {
        String NAME = "createChat";

        interface Fields {
            String OTHER_USER_IDS = "otherUserIds";
            String GROUP_NAME = "groupName";
        }
    }

    public interface MarkChatReadFunc {
        String NAME = "markChatAsRead";

        interface Fields {
            String CHAT_ID = "chatId";
        }
    }

    public interface DeleteConversationsFunc {
        String NAME = "deleteConversations";

        interface Fields {
            String CHAT_IDS = "ids";
        }
    }

    public interface GetMySubscriptionsFunc {
        String NAME = "getMySubscriptions";
    }

    public interface SendUserTypingFunc {
        String NAME = "notifyUserTyping";

        interface Fields {
            String CHAT_ID = "chatId";
        }
    }

    /*************************
     * Parse Model Conversion
     *************************/

    public static ExampleUser fromParseUser(ParseUser in) {
        return new ExampleUser(in.getObjectId(),
            in.getString(UsersTable.Fields.DISPLAY_NAME));
    }

    public static ExampleMessage from(ParseObject o, ParseHelper parseHelper) {
        if (o == null) {
            return null;
        }
        try {
            final String id = o.getObjectId();
            final String localId = o.has(MessagesTable.Fields.LOCAL_ID) ? o.getString(MessagesTable.Fields.LOCAL_ID) : null;
            final String from = o.getParseObject(MessagesTable.Fields.FROM).getObjectId();
            final long timestamp = o.getCreatedAt() != null? o.getCreatedAt().getTime() : 0;
            final Payload payload;
            final String type = o.getString(MessagesTable.Fields.TYPE);
            if (MessagesTable.Types.IMAGE.equals(type)) {
                ParseObject image = o.getParseObject(MessagesTable.Fields.IMAGE);
                final String url;
                String thumbnailUrl = null;
                if (image != null && image.has(ImagesTable.Fields.IMAGE)) {
                    url = image.getParseFile(ImagesTable.Fields.IMAGE).getUrl();
                    if (image.has(ImagesTable.Fields.THUMBNAIL)) {
                        thumbnailUrl = image.getParseFile(ImagesTable.Fields.THUMBNAIL).getUrl();
                    }
                }
                else {
                    url = ImagePayload.PLACEHOLDER;
                }
                payload = new ImagePayload(url, thumbnailUrl, o.getString(MessagesTable.Fields.MESSAGE));
            }
            else {
                payload = new TextPayload(o.getString(MessagesTable.Fields.MESSAGE));
            }
            final boolean fromMe = parseHelper.getCurrentUser().getObjectId().equals(from);
            return new ExampleMessage(id, localId, fromMe, from, payload, timestamp, false);
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to convert message with id: " + o.getObjectId(), e);
        }
    }

    public static ExampleConversation conversationFromChat(ParseObject chat, ParseHelper helper) {
        String name = chat.getString(ChatTable.Fields.NAME);
        List<BaseUser> users = Collections.emptyList(); // TODO: Populate!
        int unread = 0;
        return new ExampleConversation(chat.getObjectId(),
            name,
            users,
            from((ParseObject) chat.get(ChatTable.Fields.LAST_MESSAGE), helper),
            unread);
    }

    public static ExampleConversation conversationFromSubscription(ParseObject subscription, ParseHelper helper) {
        final ParseObject chatParseObject = subscription.getParseObject(ChatSubscriptionTable.Fields.CHAT);
        if (chatParseObject.isDataAvailable()) {
            final String name = TextUtils.isEmpty(chatParseObject.getString(ChatTable.Fields.NAME)) ? subscription.getString(ChatSubscriptionTable.Fields.NAME) : chatParseObject.getString(ChatTable.Fields.NAME);
            final List<BaseUser> users = Collections.emptyList(); // TODO: Populate!
            int unread = chatParseObject.getInt(ChatTable.Fields.MESSAGE_COUNT) - subscription.getInt(ChatSubscriptionTable.Fields.LAST_SEEN_COUNT);
            final boolean hasLastMessage = chatParseObject.has(ChatTable.Fields.LAST_MESSAGE) && chatParseObject.getParseObject(ChatTable.Fields.LAST_MESSAGE).isDataAvailable();
            final ExampleMessage lastMessage = hasLastMessage ? from(chatParseObject.getParseObject(ChatTable.Fields.LAST_MESSAGE), helper) : null;
            return new ExampleConversation(chatParseObject.getObjectId(),
                name,
                users,
                lastMessage,
                unread);
        }
        else {
            return new ExampleConversation(chatParseObject.getObjectId());
        }
    }
}

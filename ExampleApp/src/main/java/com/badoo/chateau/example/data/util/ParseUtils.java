package com.badoo.chateau.example.data.util;

import android.text.TextUtils;

import com.badoo.chateau.data.models.BaseConversation;
import com.badoo.chateau.data.models.BaseUser;
import com.badoo.chateau.data.models.BaseMessage;
import com.badoo.chateau.data.models.payloads.ImagePayload;
import com.badoo.chateau.data.models.payloads.Payload;
import com.badoo.chateau.data.models.payloads.TextPayload;
import com.badoo.chateau.core.model.Conversation;
import com.badoo.chateau.core.model.Message;
import com.badoo.chateau.core.model.User;
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

    public static User fromParseUser(ParseUser in) {
        return new BaseUser(in.getObjectId(),
            in.getString(UsersTable.Fields.DISPLAY_NAME));
    }

    public static Message baseMessageFromParseObject(ParseObject o) {
        if (o == null) {
            return null;
        }
        try {
            // TODO Refactor to factory strategy factory!
            // TODO Really important, don't forgot!
            final String id = o.getObjectId();
            final String localId = o.has(MessagesTable.Fields.LOCAL_ID) ? o.getString(MessagesTable.Fields.LOCAL_ID) : null;
            final String from = o.getParseObject(MessagesTable.Fields.FROM).getObjectId();
            final long timestamp = o.getCreatedAt().getTime();
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
            final boolean fromMe = ParseUser.getCurrentUser().getObjectId().equals(from);
            return new BaseMessage(id, localId, fromMe, from, payload, timestamp, false);
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to convert message with id: " + o.getObjectId(), e);
        }
    }

    public static Conversation conversationFromChat(ParseObject chat) {
        String name = chat.getString(ChatTable.Fields.NAME);
        List<BaseUser> users = Collections.emptyList(); // TODO: Populate!
        int unread = 0;
        return new BaseConversation(chat.getObjectId(),
            name,
            users,
            baseMessageFromParseObject((ParseObject) chat.get(ChatTable.Fields.LAST_MESSAGE)),
            unread);
    }

    public static Conversation conversationFromSubscription(ParseObject subscription) {
        final ParseObject chatParseObject = subscription.getParseObject(ChatSubscriptionTable.Fields.CHAT);
        if (chatParseObject.isDataAvailable()) {
            final String name = TextUtils.isEmpty(chatParseObject.getString(ChatTable.Fields.NAME)) ? subscription.getString(ChatSubscriptionTable.Fields.NAME) : chatParseObject.getString(ChatTable.Fields.NAME);
            final List<BaseUser> users = Collections.emptyList(); // TODO: Populate!
            int unread = chatParseObject.getInt(ChatTable.Fields.MESSAGE_COUNT) - subscription.getInt(ChatSubscriptionTable.Fields.LAST_SEEN_COUNT);
            final boolean hasLastMessage = chatParseObject.has(ChatTable.Fields.LAST_MESSAGE) && chatParseObject.getParseObject(ChatTable.Fields.LAST_MESSAGE).isDataAvailable();
            final Message lastMessage = hasLastMessage ? baseMessageFromParseObject(chatParseObject.getParseObject(ChatTable.Fields.LAST_MESSAGE)) : null;
            return new BaseConversation(chatParseObject.getObjectId(),
                name,
                users,
                lastMessage,
                unread);
        }
        else {
            return new BaseConversation(chatParseObject.getObjectId());
        }
    }
}

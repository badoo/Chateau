package com.badoo.chateau.ui.chat.messages;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.badoo.barf.mvp.View;
import com.badoo.chateau.data.models.BaseMessage;

import java.util.List;

public interface MessageListView extends View<MessageListPresenter> {

    /**
     * Set the title of the message list
     */
    void setTitle(@NonNull String title);

    /**
     * Show that the other user is typing.
     */
    void showOtherUserTyping();

    /**
     * Show that previous messages are been loaded.
     */
    void showLoadingMoreMessages(boolean show);

    /**
     * Show a given message.  This will update a message if a matching message is already been displayed.
     */
    void showMessage(@NonNull BaseMessage message);

    /**
     * Show all the given messages.  It is a assumed that these messages make up a single chunk and can be inserted together.  If not,
     * {@link #showMessage(BaseMessage)} should be called multiple times.
     */
    void showMessages(@NonNull List<BaseMessage> messages);

    /**
     * Replace a message with the same id with this message.  If a message with the same id is not found, then the message will not be
     * displayed.
     */
    void replaceMessage(@NonNull BaseMessage message);

    /**
     * Show an error message to the user.
     */
    void showGenericError();


}

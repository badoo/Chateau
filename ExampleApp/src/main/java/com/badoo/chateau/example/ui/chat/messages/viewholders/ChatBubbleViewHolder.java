package com.badoo.chateau.example.ui.chat.messages.viewholders;

import android.content.res.Resources;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.badoo.chateau.data.models.payloads.Payload;
import com.badoo.chateau.example.R;
import com.badoo.chateau.example.data.model.ExampleMessage;
import com.badoo.chateau.example.ui.util.MessageViewHolder;
import com.badoo.chateau.extras.widgets.ChatBubbleLayout;

public abstract class ChatBubbleViewHolder<P extends Payload> extends MessageViewHolder<P> {

    private final LinearLayout mRoot;

    private final int mBgColorSent;
    private final int mBgColorReceived;
    private final ChatBubbleLayout mChatBubble;

    public ChatBubbleViewHolder(View itemView) {
        super(itemView);
        mRoot = (LinearLayout) itemView;
        mChatBubble = (ChatBubbleLayout) itemView.findViewById(R.id.message_background);

        final Resources resources = mChatBubble.getResources();
        mBgColorSent = resources.getColor(R.color.bg_msg_sent);
        mBgColorReceived = resources.getColor(R.color.bg_msg_received);
    }

    @Override
    public void bind(ExampleMessage message) {
        super.bind(message);
        final boolean fromMe = message.isFromMe();
        mChatBubble.reverseLayout(!fromMe);
        mChatBubble.setBackgroundColor(fromMe ? mBgColorSent : mBgColorReceived);

        mRoot.setGravity(fromMe ? Gravity.RIGHT : Gravity.LEFT);
    }
}

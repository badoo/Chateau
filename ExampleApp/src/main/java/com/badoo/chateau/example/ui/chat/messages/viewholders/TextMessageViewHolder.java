package com.badoo.chateau.example.ui.chat.messages.viewholders;

import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badoo.chateau.example.R;
import com.badoo.chateau.data.models.BaseMessage;
import com.badoo.chateau.data.models.payloads.TextPayload;
import com.badoo.chateau.example.data.model.ExampleMessage;
import com.badoo.chateau.example.ui.util.MessageViewHolder;
import com.badoo.chateau.example.ui.widgets.TintableBackgroundFrameLayout;

public class TextMessageViewHolder extends MessageViewHolder<TextPayload> {

    private final LinearLayout mRoot;
    private final TextView mMessageText;
    private final TintableBackgroundFrameLayout mBackground;
    private final int mMargin;

    public TextMessageViewHolder(@NonNull View itemView) {
        super(itemView);
        mRoot = (LinearLayout) itemView;
        mMessageText = (TextView) itemView.findViewById(R.id.message_text);
        mBackground = (TintableBackgroundFrameLayout) itemView.findViewById(R.id.message_background);
        mMargin = itemView.getResources().getDimensionPixelSize(R.dimen.chatBubbleMargin);
    }

    @Override
    protected void bindPayload(ExampleMessage message, TextPayload payload) {
        mMessageText.setText((payload.getMessage()));

        final boolean fromMe = message.isFromMe();
        // The tint helper needs a state list so we use one where the enabled state is "from me" and the disabled state is "from other person"
        mBackground.setEnabled(fromMe);
        mBackground.setBackgroundResource(fromMe ? R.drawable.bg_chat_bubble_right : R.drawable.bg_chat_bubble_left);

        mRoot.setGravity(fromMe ? Gravity.RIGHT : Gravity.LEFT);
        final LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mBackground.getLayoutParams();
        params.leftMargin = fromMe ? mMargin : 0;
        params.rightMargin = fromMe ? 0 : mMargin;
    }
}

package com.badoo.chateau.example.ui.chat.messages.viewholders;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.badoo.chateau.data.models.payloads.TextPayload;
import com.badoo.chateau.example.R;
import com.badoo.chateau.example.data.model.ExampleMessage;

public class TextMessageViewHolder extends ChatBubbleViewHolder<TextPayload> {

    private final TextView mMessageText;

    public TextMessageViewHolder(@NonNull View itemView) {
        super(itemView);
        mMessageText = (TextView) itemView.findViewById(R.id.message_text);
    }

    @Override
    protected void bindPayload(ExampleMessage message, TextPayload payload) {
        mMessageText.setText((payload.getMessage()));
    }
}

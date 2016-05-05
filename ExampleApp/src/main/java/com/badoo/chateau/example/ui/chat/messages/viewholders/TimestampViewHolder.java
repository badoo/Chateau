package com.badoo.chateau.example.ui.chat.messages.viewholders;

import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

import com.badoo.chateau.example.R;
import com.badoo.chateau.data.models.BaseMessage;
import com.badoo.chateau.data.models.payloads.TimestampPayload;
import com.badoo.chateau.example.data.model.ExampleMessage;
import com.badoo.chateau.example.ui.util.MessageViewHolder;

public class TimestampViewHolder extends MessageViewHolder<TimestampPayload> {

    private final TextView mTimestamp;

    public TimestampViewHolder(@NonNull View itemView) {
        super(itemView);
        mTimestamp = (TextView) itemView.findViewById(R.id.day_timestamp);
    }

    @Override
    protected void bindPayload(ExampleMessage message, TimestampPayload payload) {
        mTimestamp.setText(DateUtils.getRelativeTimeSpanString(message.getTimestamp(), System.currentTimeMillis(), DateUtils.DAY_IN_MILLIS));
    }
}

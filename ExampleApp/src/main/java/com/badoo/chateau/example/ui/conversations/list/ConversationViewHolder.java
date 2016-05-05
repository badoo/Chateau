package com.badoo.chateau.example.ui.conversations.list;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

import com.badoo.chateau.example.R;
import com.badoo.chateau.data.models.payloads.ImagePayload;
import com.badoo.chateau.data.models.payloads.TextPayload;
import com.badoo.chateau.example.data.model.ExampleConversation;
import com.badoo.chateau.example.data.model.ExampleMessage;
import com.badoo.chateau.example.ui.util.BindableViewHolder;
import com.badoo.chateau.extras.MultiSelectionHelper;
import com.badoo.chateau.extras.ViewFinder;

import java.text.DateFormat;
import java.util.Date;

class ConversationViewHolder extends BindableViewHolder<ExampleConversation> implements View.OnClickListener, View.OnLongClickListener {

    public static final String CAMERA_EMOJI = "\uD83D\uDCF7";

    private final View mRoot;
    @NonNull
    private final OnConversationClickedListener mClickListener;
    @NonNull
    private final MultiSelectionHelper mSelectionHelper;
    private final TextView mName;
    private final TextView mLastMessageTime;
    private final TextView mLastMessage;
    private final TextView mUnreadCount;
    private final DateFormat mTimeFormat;
    private final DateFormat mDateFormat;

    public ConversationViewHolder(@NonNull View view, @NonNull OnConversationClickedListener clickListener, @NonNull MultiSelectionHelper selectionHelper) {
        super(view);
        mRoot = view;
        mClickListener = clickListener;
        mSelectionHelper = selectionHelper;
        final ViewFinder finder = ViewFinder.from(view);
        mTimeFormat = android.text.format.DateFormat.getTimeFormat(itemView.getContext());
        mDateFormat = android.text.format.DateFormat.getMediumDateFormat(itemView.getContext());
        mName = finder.findViewById(R.id.conversation_name);
        mLastMessageTime = finder.findViewById(R.id.conversation_last_message_time);
        mLastMessage = finder.findViewById(R.id.conversation_last_message);
        mUnreadCount = finder.findViewById(R.id.conversation_unread_count);

        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
    }

    @Override
    public void bind(ExampleConversation conversation) {
        super.bind(conversation);
        mRoot.setSelected(mSelectionHelper.isPositionSelected(getAdapterPosition()));
        mName.setText(conversation.getName());

        final ExampleMessage lastMessage = conversation.getLastMessage();
        if (lastMessage != null) {
            handlePayloadRendering(lastMessage);
            handleTimestampRendering(lastMessage);
        }
        else {
            mLastMessage.setText("");
            mLastMessageTime.setText("");
        }
        final int unreadCount = conversation.getUnreadCount();
        if (unreadCount != 0) {
            mUnreadCount.setText(String.valueOf(unreadCount));
            mUnreadCount.setVisibility(View.VISIBLE);
        }
        else {
            mUnreadCount.setVisibility(View.INVISIBLE);
        }
    }

    private void handleTimestampRendering(ExampleMessage lastMessage) {
        final boolean isToday = DateUtils.isToday(lastMessage.getTimestamp());
        final Date date = new Date(lastMessage.getTimestamp());
        if (isToday) {
            mLastMessageTime.setText(mTimeFormat.format(date));
        }
        else {
            mLastMessageTime.setText(mDateFormat.format(date));
        }
    }

    private void handlePayloadRendering(ExampleMessage lastMessage) {
        if (lastMessage.getPayload() instanceof TextPayload) {
            mLastMessage.setText(((TextPayload) lastMessage.getPayload()).getMessage());
        }
        else if (lastMessage.getPayload() instanceof ImagePayload) {
            final ImagePayload payload = (ImagePayload) lastMessage.getPayload();
            if (TextUtils.isEmpty(payload.getMessage())) {
                // Add the camera emoji since it's only supported in xml on Android 6+
                mLastMessage.setText(mLastMessage.getResources().getString(R.string.info_last_message_image, CAMERA_EMOJI));
            }
            else {
                mLastMessage.setText(payload.getMessage());
            }
        }
        else {
            throw new IllegalArgumentException("Message not supported: " + lastMessage);
        }
    }

    @Override
    public void onClick(View v) {
        if (!mSelectionHelper.onClick(getAdapterPosition())) {
            mClickListener.onConversationClicked(getBoundItem());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        return mSelectionHelper.onLongClick(getAdapterPosition());
    }

    public interface OnConversationClickedListener {

        void onConversationClicked(@NonNull ExampleConversation conversation);
    }
}

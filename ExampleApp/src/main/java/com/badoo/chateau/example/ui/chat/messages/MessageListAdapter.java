package com.badoo.chateau.example.ui.chat.messages;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.badoo.chateau.data.models.payloads.Payload;
import com.badoo.chateau.example.data.model.ExampleMessage;
import com.badoo.chateau.example.ui.util.MessageViewHolder;
import com.badoo.chateau.extras.recycle.ViewHolderFactory;
import com.badoo.chateau.extras.recycle.ViewHolderFactoryResolver;

import java.util.ArrayList;
import java.util.List;

class MessageListAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    private final ViewHolderFactoryResolver<MessageViewHolder<? extends Payload>> mFactoryResolver = new ViewHolderFactoryResolver<>();
    private final SparseArray<ItemClickListener> mListenersByViewType = new SparseArray<>();

    private final List<ItemPreProcessor> mPreProcessors = new ArrayList<>();

    private final List<ExampleMessage> mMessages = new ArrayList<>();

    @Nullable
    private ExampleMessage mShowTimestampFor;
    private ExampleMessage mOldestMessage;

    public MessageListAdapter() {
        setHasStableIds(true);
    }

    /**
     * Register a view holder factory for a given payload type.
     */
    public <T extends Payload> void registerMessageViewHolderFactory(@NonNull Class<T> type, @NonNull ViewHolderFactory<MessageViewHolder<T>> factory) {
        mFactoryResolver.registerFactory(type, factory);
    }

    /**
     * Register a view holder factory for a given payload type as well as a click listener for items of that type.
     */
    public <T extends Payload> void registerMessageViewHolderFactory(@NonNull Class<T> type, @NonNull ViewHolderFactory<MessageViewHolder<T>> factory,
                                                                     @NonNull ItemClickListener listener) {
        final int viewType = mFactoryResolver.registerFactory(type, factory);
        mListenersByViewType.put(viewType, listener);
    }

    public void registerPreProcessor(@NonNull ItemPreProcessor preProcessor) {
        mPreProcessors.add(preProcessor);
    }

    /**
     * Sets the messages to be displayed
     */
    public void setMessages(@NonNull List<ExampleMessage> messages) {
        if (!messages.isEmpty()) {
            mOldestMessage = messages.get(0);
        }
        List<ExampleMessage> processedMessages = messages;
        for (ItemPreProcessor preProcessor : mPreProcessors) {
            processedMessages = preProcessor.doProcess(messages);
        }
        mMessages.clear();
        mMessages.addAll(processedMessages);
        notifyDataSetChanged();
    }

    public void addNewMessages(@NonNull List<ExampleMessage> messages) {
        final int oldSize = mMessages.size();
        mMessages.addAll(messages);
        notifyItemRangeInserted(oldSize, messages.size());
    }

    public void addOldMessages(@NonNull List<ExampleMessage> messages) {
        mMessages.addAll(0, messages);
        notifyItemRangeInserted(0, messages.size());
    }

    public void replaceMessage(@Nullable ExampleMessage oldMessage, @NonNull ExampleMessage newMessage) {
        if (oldMessage == null) {
            oldMessage = newMessage;
        }
        for (int i = mMessages.size() - 1; i >= 0; i--) {
            ExampleMessage candidate = mMessages.get(i);
            final boolean replacingLocalMessage = candidate.isUnconfirmed() && candidate.getLocalId().equals(oldMessage.getLocalId());
            final boolean replacingConfirmedMessage = !candidate.isUnconfirmed() && candidate.getId().equals(oldMessage.getId());
            if (replacingLocalMessage || replacingConfirmedMessage) {
                mMessages.set(i, newMessage);
                notifyItemChanged(i);
                return;
            }
        }
    }

    /**
     * Toggles showing the time stamp for a given message.  If the message passed as the argument is currently displaying the timestamp
     * it will hide the timestamp.  Otherwise it will hide the currently showing timestamp and show the time stamp for the message passed.
     */
    public void toggleTimestampForMessage(@Nullable ExampleMessage message) {
        final int currentTimestampShownIndex = mMessages.indexOf(mShowTimestampFor);
        final int newTimestampShownIndex = mMessages.indexOf(message);

        if (mShowTimestampFor == message) {
            mShowTimestampFor = null;
        }
        else {
            mShowTimestampFor = message;
            notifyItemChanged(currentTimestampShownIndex);
        }
        notifyItemChanged(newTimestampShownIndex);
    }

    @VisibleForTesting
    ExampleMessage getMessage(int position) {
        return mMessages.get(position);
    }

    public ExampleMessage getOldestMessage() {
        return mOldestMessage;
    }

    public int getIndexOfMessage(ExampleMessage message) {
        return mMessages.indexOf(message);
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Attempt to get the view holder factory for the given view type, there should be one registered via
        // #registerMessageViewHolderFactory
        final MessageViewHolder<? extends Payload> messageViewHolder = mFactoryResolver.getFactoryForId(viewType).create(parent);
        final View itemView = messageViewHolder.itemView;
        final ItemClickListener itemClickListener = mListenersByViewType.get(viewType);
        if (itemClickListener != null) {
            itemView.setOnClickListener(v -> itemClickListener.onClick(messageViewHolder.getBoundItem()));
            itemView.setOnLongClickListener(v -> itemClickListener.onLongPress(messageViewHolder.getBoundItem()));
        }

        return messageViewHolder;
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        //noinspection unchecked
        final ExampleMessage message = mMessages.get(position);
        holder.bind(message);
        holder.showTimestamp(message == mShowTimestampFor);
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public long getItemId(int position) {
        ExampleMessage message = getMessage(position);
        if (message.getLocalId() == null) {
            return message.hashCode();
        }
        else {
            // Create stable id from local id hash & payload hash
            return message.getLocalId().hashCode() * 31 + message.getPayload().hashCode();
        }
    }

    @Override
    public int getItemViewType(int position) {
        final ExampleMessage message = mMessages.get(position);
        return mFactoryResolver.getIdForType(message.getPayload().getClass());
    }

    /**
     * Item click listener handling both normal and long presses
     */
    public interface ItemClickListener {

        void onClick(@NonNull ExampleMessage message);

        boolean onLongPress(@NonNull ExampleMessage message);
    }

}

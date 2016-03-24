package com.badoo.chateau.example.ui.chat.messages;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.badoo.chateau.data.models.BaseMessage;
import com.badoo.chateau.data.models.payloads.Payload;
import com.badoo.chateau.example.ui.util.BaseMessageViewHolder;
import com.badoo.chateau.example.ui.util.recycle.DataObserverProxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MessageListAdapter extends RecyclerView.Adapter<BaseMessageViewHolder> {

    private final ViewHolderFactoryResolver<BaseMessageViewHolder<? extends Payload>> mFactoryResolver = new ViewHolderFactoryResolver<>();
    private final SparseArray<ItemClickListener> mListenersByViewType = new SparseArray<>();

    private final List<ItemPreProcessor> mPreProcessors = new ArrayList<>();

    private final Map<String, BaseMessage> mUnconfirmedMessages = new HashMap<>();
    private final List<BaseMessage> mMessages = new ArrayList<>();

    private DataObserverProxy mDataObserverProxy = new DataObserverProxy(this);

    @Nullable
    private BaseMessage mShowTimestampFor;

    /**
     * Register a view holder factory for a given payload type.
     */
    public <T extends Payload> void registerMessageViewHolderFactory(@NonNull Class<T> type, @NonNull ViewHolderFactory<BaseMessageViewHolder<T>> factory) {
        mFactoryResolver.registerFactory(type, factory);
    }

    /**
     * Register a view holder factory for a given payload type as well as a click listener for items of that type.
     */
    public <T extends Payload> void registerMessageViewHolderFactory(@NonNull Class<T> type, @NonNull ViewHolderFactory<BaseMessageViewHolder<T>> factory,
                                                                     @NonNull ItemClickListener listener) {
        final int viewType = mFactoryResolver.registerFactory(type, factory);
        mListenersByViewType.put(viewType, listener);
    }


    public void registerPreProcessor(@NonNull ItemPreProcessor preProcessor) {
        mPreProcessors.add(preProcessor);
    }

    /**
     * Will add the message to the list.  If this message confirms a messages that message will be removed from the list and replaced
     * with the new message.  Returns <code>true</code> if the message has been inserted at the bottom of the list.
     */
    public boolean addMessage(@NonNull BaseMessage message) {
        trackMessageIfUnconfirmed(message);

        if (replaceUnconfirmedVersion(message)) {
            // Just replaced an unconfirmed message, nothing should have changed.
            return false;
        }

        final List<BaseMessage> messages = new ArrayList<>();
        messages.add(message);

        return processAndInsert(messages);
    }


    /**
     * Will add the message to the list.  It is assumed that the messages represent a block of messages such that they can
     * be inserted together rather than been spread out across the current message list.  Returns <code>true</code> if the
     * messages were inserted at the bottom of the list.
     * <b>This does not support the updating of unconfirmed messages</b>
     */
    public boolean addMessages(@NonNull List<BaseMessage> messages) {
        return processAndInsert(messages);
    }

    /**
     * Attempt to replace a message with the same id with this new message if that message current exists in the list.
     */
    public void replaceMessage(@NonNull BaseMessage message) {
        for (int i = 0; i < mMessages.size(); i++) {
            if (message.getId().equals(mMessages.get(i).getId())) {
                mMessages.set(i, message);
                notifyItemChanged(i);
                break;
            }
        }
    }

    /**
     * Toggles showing the time stamp for a given message.  If the message passed as the argument is currently displaying the timestamp
     * it will hide the timestamp.  Otherwise it will hide the currently showing timestamp and show the time stamp for the message passed.
     */
    public void toggleTimestampForMessage(@Nullable BaseMessage message) {
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
    BaseMessage getMessage(int position) {
        return mMessages.get(position);
    }

    /**
     * If the message is unconfirmed, keep track of it so that when the confirmation is received it can be easily updated.
     */
    private void trackMessageIfUnconfirmed(@NonNull BaseMessage newMessage) {
        if (newMessage.isUnconfirmed() && !newMessage.isFailedToSend()) {
            mUnconfirmedMessages.put(newMessage.getLocalId(), newMessage);
        }
    }

    /**
     * Check to see if the message is a confirmed version of an unconfirmed message that is currently been tracked.  If this is the case
     * remove it from the current messages, and stop tracking that unconfirmed message.
     */
    private boolean replaceUnconfirmedVersion(@NonNull BaseMessage message) {
        if (message.isUnconfirmed() && !message.isFailedToSend()) {
            return false; // Unconfirmed messages that are not Failure messages should never replace existing ones
        }
        final BaseMessage unconfirmedVersion = mUnconfirmedMessages.remove(message.getLocalId());
        if (unconfirmedVersion != null) {
            final int index = mMessages.indexOf(unconfirmedVersion);
            if (index != -1) {
                mMessages.set(index, message);
                notifyItemChanged(index);
                return true;
            }
        }
        return false;
    }

    private boolean processAndInsert(@NonNull List<BaseMessage> newMessages) {
        int insertionPoint = findInsertionPoint(newMessages.get(0));

        for (ItemPreProcessor preProcessor : mPreProcessors) {
            insertionPoint = preProcessor.doProcess(newMessages, mMessages, insertionPoint, mDataObserverProxy);
        }

        final boolean insertedAtEnd = insertionPoint == mMessages.size();
        mMessages.addAll(insertionPoint, newMessages);
        notifyItemRangeInserted(insertionPoint, newMessages.size());

        return insertedAtEnd;
    }

    private int findInsertionPoint(BaseMessage firstMessage) {
        if (mMessages.isEmpty()) {
            return 0;
        }

        for (int i = 0; i < mMessages.size(); i++) {
            final BaseMessage msg = mMessages.get(i);
            if (firstMessage.getTimestamp() < msg.getTimestamp()) {
                return i;
            }
        }

        return mMessages.size();
    }

    @Override
    public BaseMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Attempt to get the view holder factory for the given view type, there should be one registered via
        // #registerMessageViewHolderFactory
        final BaseMessageViewHolder<? extends Payload> baseMessageViewHolder = mFactoryResolver.getFactoryForId(viewType).create(parent);
        final View itemView = baseMessageViewHolder.itemView;
        final ItemClickListener itemClickListener = mListenersByViewType.get(viewType);
        if (itemClickListener != null) {
            itemView.setOnClickListener(v -> itemClickListener.onClick(baseMessageViewHolder.getBoundItem()));
            itemView.setOnLongClickListener(v -> itemClickListener.onLongPress(baseMessageViewHolder.getBoundItem()));
        }

        return baseMessageViewHolder;
    }

    @Override
    public void onBindViewHolder(BaseMessageViewHolder holder, int position) {
        //noinspection unchecked
        final BaseMessage message = mMessages.get(position);
        holder.bind(message);
        holder.showTimestamp(message == mShowTimestampFor);
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        final BaseMessage message = mMessages.get(position);
        return mFactoryResolver.getIdForType(message.getPayload().getClass());
    }


    public interface ItemClickListener {
        void onClick(@NonNull BaseMessage message);

        boolean onLongPress(@NonNull BaseMessage message);
    }

    public interface ItemPreProcessor {

        /**
         * Pre processes the given new messages before they are inserted at the given point in to the current messages list.  This method can modify
         * the current messages list, but if this would cause the insertion point to be be modified, the new insertion point must be returned
         * by this method.  If the insertion point hasn't been modified, it must return the given insertion point.  Any updates made to the current
         * messages list must be reflected by calls to the data observer.
         */
        int doProcess(@NonNull List<BaseMessage> newMessages, @NonNull List<BaseMessage> currentMessages, int insertionPoint, @NonNull RecyclerView.AdapterDataObserver dataObserver);
    }
}

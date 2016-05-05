package com.badoo.chateau.example.ui.chat.messages;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.badoo.chateau.data.models.BaseMessage;
import com.badoo.chateau.example.data.model.ExampleMessage;

public class BaseItemClickListener implements MessageListAdapter.ItemClickListener {

    @NonNull
    private final RecyclerView mRecyclerView;
    private MessageListAdapter mAdapter;

    BaseItemClickListener(@NonNull RecyclerView recyclerView, @NonNull MessageListAdapter adapter) {
        mRecyclerView = recyclerView;
        mAdapter = adapter;
    }

    @Override
    public void onClick(@NonNull ExampleMessage message) {
        if(!(mRecyclerView.isComputingLayout() || mRecyclerView.isAnimating())) {
            mAdapter.toggleTimestampForMessage(message);
        }
    }

    @Override
    public boolean onLongPress(@NonNull ExampleMessage message) {
        return true;
    }
}

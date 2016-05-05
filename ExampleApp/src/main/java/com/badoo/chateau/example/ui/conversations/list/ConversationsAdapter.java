package com.badoo.chateau.example.ui.conversations.list;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.badoo.chateau.example.R;
import com.badoo.chateau.example.data.model.ExampleConversation;
import com.badoo.chateau.extras.MultiSelectionHelper;

import java.util.ArrayList;
import java.util.List;

class ConversationsAdapter extends RecyclerView.Adapter<ConversationViewHolder> {

    private static final String TAG = ConversationsAdapter.class.getSimpleName();

    @NonNull
    private final ConversationViewHolder.OnConversationClickedListener mClickedListener;
    private MultiSelectionHelper mSelectionHelper;
    private List<ExampleConversation> mConversations = new ArrayList<>();

    public ConversationsAdapter(@NonNull ConversationViewHolder.OnConversationClickedListener clickedListener) {
        mClickedListener = clickedListener;
    }

    public void setSelectionHelper(@NonNull MultiSelectionHelper selectionHelper) {
        mSelectionHelper = selectionHelper;
    }

    /**
     * Sets the list of conversations to show, clearing any previously loaded conversations.
     */
    public void setConversations(List<ExampleConversation> conversations) {
        mConversations.clear();
        mConversations.addAll(conversations);
        notifyDataSetChanged();
    }

    /**
     * Returns the list of conversations which as selected by the user (to be deleted)
     */
    @NonNull
    public List<ExampleConversation> getSelectedConversations() {
        ArrayList<ExampleConversation> selected = new ArrayList<>();
        for (Integer position : mSelectionHelper.getSelectedItems()) {
            selected.add(mConversations.get(position));
        }
        return selected;
    }

    @Override
    public ConversationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ConversationViewHolder(
            LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_conversation, parent, false), mClickedListener, mSelectionHelper);
    }

    @Override
    public void onBindViewHolder(ConversationViewHolder holder, int position) {
        holder.bind(mConversations.get(position));
    }

    @Override
    public int getItemCount() {
        return mConversations.size();
    }

}

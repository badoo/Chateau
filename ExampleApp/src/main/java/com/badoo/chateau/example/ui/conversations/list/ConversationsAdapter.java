package com.badoo.chateau.example.ui.conversations.list;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.badoo.chateau.example.R;
import com.badoo.chateau.data.models.BaseConversation;
import com.badoo.chateau.example.ui.util.MultiSelectionHelper;
import com.badoo.chateau.core.model.Conversation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

class ConversationsAdapter extends RecyclerView.Adapter<ConversationViewHolder> {

    private static final String TAG = ConversationsAdapter.class.getSimpleName();

    @NonNull
    private final ConversationViewHolder.OnConversationClickedListener mClickedListener;
    private MultiSelectionHelper mSelectionHelper;
    private List<BaseConversation> mConversations = new ArrayList<>();

    public ConversationsAdapter(@NonNull ConversationViewHolder.OnConversationClickedListener clickedListener) {
        mClickedListener = clickedListener;
    }

    public void setSelectionHelper(@NonNull MultiSelectionHelper selectionHelper) {
        mSelectionHelper = selectionHelper;
    }

    /**
     * Sets the list of conversations to show, clearing any previously loaded conversations.
     */
    public void setConversations(List<BaseConversation> conversations) {
        mConversations.clear();
        mConversations.addAll(conversations);
        notifyDataSetChanged();
    }

    /**
     * Update a conversation already in the list or prepend it to the top if it is not. For existing conversations,
     * if the timestamp was changed the item may change position.
     * @return true if the list should scroll to the top to reveal the conversation
     */
    public boolean updateConversation(@NonNull BaseConversation conversation) {
        int previousIndex = -1;
        for (int i = 0; i < mConversations.size(); i++) {
            if (mConversations.get(i).getId().equals(conversation.getId())) {
                previousIndex = i;
                break;
            }
        }
        Log.d(TAG, "Update conversation " + conversation.getId() + " at " + previousIndex);
        if (previousIndex == -1) {
            // This is a new conversation, just add it to the top
            if (mConversations.isEmpty()) {
                mConversations.add(conversation);
            }
            else {
                mConversations.add(0, conversation);
            }
            notifyItemInserted(0);
            return true;
        }
        else {
            mConversations.remove(previousIndex);
            mConversations.add(0, conversation);
            notifyItemChanged(previousIndex);
            notifyItemMoved(previousIndex, 0);
            return false;
        }
    }

    /**
     * Removes a number of conversations from the list, if they are present.
     */
    public void removeConversations(@NonNull List<BaseConversation> conversations) {
        Log.d(TAG, "Removing " + conversations.size() + " conversations");
        if (conversations.size() == 1) {
            // Animate the update for this simple case
            final String id = conversations.get(0).getId();
            for (int i = 0; i < mConversations.size(); i++) {
                if (mConversations.get(i).getId().equals(id)) {
                    mConversations.remove(i);
                    notifyItemRemoved(i);
                    break;
                }
            }
        }
        else {
            // Otherwise just refresh the entire list after removing the items
            Set<String> ids = new HashSet<>();
            for (Conversation c : conversations) {
                ids.add(((BaseConversation) c).getId());
            }
            Iterator<BaseConversation> iter = mConversations.iterator();
            while (iter.hasNext()) {
                BaseConversation conversation = iter.next();
                if (ids.contains(conversation.getId())) {
                    iter.remove();
                }
            }
            notifyDataSetChanged();
        }
    }

    /**
     * Returns the list of conversations which as selected by the user (to be deleted)
     */
    @NonNull
    public List<BaseConversation> getSelectedConversations() {
        ArrayList<BaseConversation> selected = new ArrayList<>();
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

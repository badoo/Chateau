package com.badoo.chateau.example.ui.conversations.list;

import android.support.v7.widget.RecyclerView;

import com.badoo.chateau.data.models.BaseConversation;
import com.badoo.chateau.example.ui.util.MultiSelectionHelper;
import com.badoo.chateau.example.ui.utils.TestUtils;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ConversationsAdapterTest {

    private ConversationViewHolder.OnConversationClickedListener mClickListener;
    private ConversationsAdapter mAdapter;
    private RecyclerView.AdapterDataObserver mDataObserver;
    private MultiSelectionHelper mSelectionHelper;

    @Before
    public void setupAdapter() throws Exception {
        mClickListener = mock(ConversationViewHolder.OnConversationClickedListener.class);
        mSelectionHelper = mock(MultiSelectionHelper.class);
        mAdapter = new ConversationsAdapter(mClickListener);
        mAdapter.setSelectionHelper(mSelectionHelper);
        mDataObserver = TestUtils.fixAdapterForTesting(mAdapter);
    }

    @Test
    public void testNewAdapterIsEmpty() {
        assertEquals(0, mAdapter.getItemCount());
    }

    @Test
    public void setSetConversations() {
        // Given
        List<BaseConversation> conversations = createConversations(10);

        // When
        mAdapter.setConversations(conversations);

        // Then
        verify(mDataObserver).onChanged();
        assertEquals(conversations.size(), mAdapter.getItemCount());
    }

    @Test
    public void updateConversationWithEmptyList() {
        // When
        mAdapter.updateConversation(createConversation(0));

        // Then
        assertEquals(1, mAdapter.getItemCount());
        verify(mDataObserver).onItemRangeInserted(0, 1);
    }

    @Test
    public void updateConversationNotInList() {
        // Given
        mAdapter.setConversations(createConversations(10));

        // When
        mAdapter.updateConversation(createConversation(20));

        // Then
        assertEquals(11, mAdapter.getItemCount());
        verify(mDataObserver).onItemRangeInserted(0, 1);
    }

    @Test
    public void updateConversationInList() {
        // Given
        mAdapter.setConversations(createConversations(10));

        // When
        mAdapter.updateConversation(createConversation(5));

        // Then
        assertEquals(10, mAdapter.getItemCount());
        verify(mDataObserver).onItemRangeChanged(5, 1, null);
    }

    @Test
    public void getSelectedItemsNoSelection() {
        // Given
        when(mSelectionHelper.getSelectedItems()).thenReturn(Collections.emptySet());

        // When
        List<BaseConversation> selected = mAdapter.getSelectedConversations();

        // Then
        assertEquals(0, selected.size());
    }

    @Test
    public void removeSingleItem() {
        // Given
        mAdapter.setConversations(createConversations(10));

        // When
        mAdapter.removeConversations(Collections.singletonList(createConversation(5)));

        // Then
        assertEquals(9, mAdapter.getItemCount());
        verify(mDataObserver).onItemRangeRemoved(5, 1);
    }

    @Test
    public void removeMultipleItems() {
        // Given
        mAdapter.setConversations(createConversations(10));
        List<BaseConversation> itemsToRemove = new ArrayList<>();
        itemsToRemove.add(createConversation(0));
        itemsToRemove.add(createConversation(3));
        itemsToRemove.add(createConversation(6));

        // When
        mAdapter.removeConversations(itemsToRemove);

        // Then
        assertEquals(7, mAdapter.getItemCount());
        verify(mDataObserver, times(2)).onChanged();
    }

    @Test
    public void getSelectedItems() {
        // Given
        Set<Integer> selectedIds = new HashSet<>();
        selectedIds.add(0);
        selectedIds.add(3);
        selectedIds.add(8);
        when(mSelectionHelper.getSelectedItems()).thenReturn(selectedIds);
        mAdapter.setConversations(createConversations(10));

        // When
        List<BaseConversation> selected = mAdapter.getSelectedConversations();

        // Then
        assertEquals(3, selected.size());
    }

    private List<BaseConversation> createConversations(int count) {
        List<BaseConversation> conversations = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            conversations.add(createConversation(i));
        }
        return conversations;
    }

    private BaseConversation createConversation(int id) {
        return new BaseConversation(Integer.toString(id), "convo" + id, Collections.emptyList(), null, 0);
    }

}
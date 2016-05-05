package com.badoo.chateau.example.ui.conversations.list;

import android.support.v7.widget.RecyclerView;

import com.badoo.chateau.example.data.model.ExampleConversation;
import com.badoo.chateau.example.ui.utils.TestUtils;
import com.badoo.chateau.extras.MultiSelectionHelper;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
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
        List<ExampleConversation> conversations = createConversations(10);

        // When
        mAdapter.setConversations(conversations);

        // Then
        verify(mDataObserver).onChanged();
        assertEquals(conversations.size(), mAdapter.getItemCount());
    }

    @Test
    public void updateConversationWithEmptyList() {
        // When
        mAdapter.setConversations(Collections.singletonList(createConversation(0)));

        // Then
        assertEquals(1, mAdapter.getItemCount());
        verify(mDataObserver).onChanged();
    }

    @Test
    public void getSelectedItemsNoSelection() {
        // Given
        when(mSelectionHelper.getSelectedItems()).thenReturn(Collections.emptySet());

        // When
        List<ExampleConversation> selected = mAdapter.getSelectedConversations();

        // Then
        assertEquals(0, selected.size());
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
        List<ExampleConversation> selected = mAdapter.getSelectedConversations();

        // Then
        assertEquals(3, selected.size());
    }

    private List<ExampleConversation> createConversations(int count) {
        List<ExampleConversation> conversations = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            conversations.add(createConversation(i));
        }
        return conversations;
    }

    private ExampleConversation createConversation(int id) {
        return new ExampleConversation(Integer.toString(id), "convo" + id, Collections.emptyList(), null, 0);
    }

}
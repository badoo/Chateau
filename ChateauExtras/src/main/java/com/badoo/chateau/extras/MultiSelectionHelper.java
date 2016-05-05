package com.badoo.chateau.extras;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import java.util.HashSet;
import java.util.Set;

/**
 * Helper for managing the selection state when performing multiple selection in a RecyclerView.
 */
public class MultiSelectionHelper {

    @IntDef({MODE_SINGLE_SELECT, MODE_MULTIPLE_SELECT})
    public @interface Mode {
    }

    public static final int MODE_SINGLE_SELECT = 0;
    public static final int MODE_MULTIPLE_SELECT = 1;

    private final RecyclerView.Adapter<?> mAdapter;
    private final OnModeChangedListener mModeChangedListener;
    @Nullable
    private final OnSelectionChangedListener mSelectionChangedListener;

    @Mode
    public int mMode = MODE_SINGLE_SELECT;

    public Set<Integer> mSelectedItems = new HashSet<>();

    public MultiSelectionHelper(@NonNull RecyclerView.Adapter<?> adapter, @NonNull OnModeChangedListener modeChangedListener, @Nullable OnSelectionChangedListener selectionChangedListener) {
        mAdapter = adapter;
        mModeChangedListener = modeChangedListener;
        mSelectionChangedListener = selectionChangedListener;
    }

    /**
     * Switch to a different selection mode. Mode switches can also occur automatically if onLongClick() is called.
     */
    public void setMode(@Mode int mode) {
        if (mMode == mode) {
            return;
        }
        mMode = mode;
        mModeChangedListener.onModeChanged(mode);
    }

    /**
     * Returns the current selection mode
     */
    @MultiSelectionHelper.Mode
    public int getMode() {
        return mMode;
    }

    /**
     * To be invoked when an item in the list is clicked (even when not in multi selection mode).
     *
     * @return true if the click was handled and nothing further should be done, false if it should be handled as a normal (not selection) click.
     */
    public boolean onClick(int position) {
        if (mMode == MODE_MULTIPLE_SELECT) {
            if (mSelectedItems.contains(position)) {
                mSelectedItems.remove(position);
                if (mSelectedItems.isEmpty()) {
                    setMode(MODE_SINGLE_SELECT);
                }
            }
            else {
                mSelectedItems.add(position);
            }
            notifySelectionChanged(position);
            return true;
        }
        return false;
    }

    /**
     * To be invoked when an item in the list is long-clicked (even when not in multi selection mode).
     *
     * @return true if the click was handled and nothing further should be done, false if it should be handled as a normal (not selection) click.
     */
    public boolean onLongClick(int position) {
        if (mMode == MODE_SINGLE_SELECT) {
            mSelectedItems.add(position);
            notifySelectionChanged(position);
            setMode(MODE_MULTIPLE_SELECT);
            return true;
        }
        return false;
    }

    /**
     * Clears the information about which items are selected. Should be called when we are done selecting items (e.g. if action is taken or selection is cancelled)
     */
    public void clearSelectedPositions() {
        mSelectedItems.clear();
        mAdapter.notifyDataSetChanged();
        setMode(MODE_SINGLE_SELECT);
    }


    /**
     * Returns whether or not a certain position is selection
     */
    public boolean isPositionSelected(int position) {
        return mSelectedItems.contains(position);
    }

    /**
     * Returns a set containing the positions of all selected items
     */
    @NonNull
    public Set<Integer> getSelectedItems() {
        return mSelectedItems;
    }

    private void notifySelectionChanged(int position) {
        if (mSelectionChangedListener != null) {
            mSelectionChangedListener.onSelectionChanged(mSelectedItems.size());
        }
        mAdapter.notifyItemChanged(position);
    }

    /**
     * Callback interface for notifying when the selection mode changes
     */
    public interface OnModeChangedListener {

        /**
         * Invoked when the selection mode changes
         */
        void onModeChanged(@Mode int multiSelect);
    }

    /**
     * Callback interface for notifying when the selection changes
     */
    public interface OnSelectionChangedListener {

        /**
         * Invoked when the number of selected items change
         *
         * @param count the number of selected items
         */
        void onSelectionChanged(int count);
    }
}

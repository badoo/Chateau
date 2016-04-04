package com.badoo.chateau.extras;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import java.util.HashSet;
import java.util.Set;

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

    public boolean onClick(int position) {
        if (mMode == MODE_MULTIPLE_SELECT) {
            if (mSelectedItems.contains(position)) {
                mSelectedItems.remove(position);
                if (mSelectedItems.isEmpty()) {
                    switchTo(MODE_SINGLE_SELECT);
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

    private void notifySelectionChanged(int position) {
        if (mSelectionChangedListener != null) {
            mSelectionChangedListener.onSelectionChanged(mSelectedItems.size());
        }
        mAdapter.notifyItemChanged(position);
    }

    public boolean onLongClick(int position) {
        if (mMode == MODE_SINGLE_SELECT) {
            mSelectedItems.add(position);
            notifySelectionChanged(position);
            switchTo(MODE_MULTIPLE_SELECT);
            return true;
        }
        return false;
    }

    public void clearSelectedPositions() {
        mSelectedItems.clear();
        mAdapter.notifyDataSetChanged();
        switchTo(MODE_SINGLE_SELECT);
    }

    public boolean isPositionSelected(int position) {
        return mSelectedItems.contains(position);
    }

    public int getMode() {
        return mMode;
    }

    public Set<Integer> getSelectedItems() {
        return mSelectedItems;
    }

    public void switchTo(@Mode int mode) {
        if (mMode == mode) return;

        mMode = mode;
        mModeChangedListener.onModeChanged(mode);
    }


    public interface OnModeChangedListener {

        void onModeChanged(@Mode int multiSelect);
    }

    public interface OnSelectionChangedListener {

        void onSelectionChanged(int count);
    }
}

package com.badoo.chateau.example.ui.util.recycle;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Item decoration that ensures that enough padding is added to the last item in the list to ensure that it scrolls above the
 * floating action button.
 */
public final class AboveFabItemDecoration extends RecyclerView.ItemDecoration {

    private final FloatingActionButton mFab;

    public AboveFabItemDecoration(@NonNull FloatingActionButton fab) {
        mFab = fab;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        final int itemPosition = parent.getChildAdapterPosition(view);
        if (itemPosition == RecyclerView.NO_POSITION) {
            return;
        }

        final int itemCount = state.getItemCount();
        if (itemCount > 0 && itemPosition == itemCount - 1) {
            outRect.set(0, 0, 0, mFab.getHeight());
        }
    }

}

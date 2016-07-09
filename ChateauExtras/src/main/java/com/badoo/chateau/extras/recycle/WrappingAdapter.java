package com.badoo.chateau.extras.recycle;

import android.support.v7.widget.RecyclerView;

/**
 * Interface to use when one adapter wraps another adapter.  Can be used to get the wrapped adapter, and the modified position for that
 * adapter.
 */
public interface WrappingAdapter<ViewHolder extends RecyclerView.ViewHolder> {

    /**
     * Assert if the current position is handled by the wrapped adapter.
     */
    boolean isHandledByWrappedAdapter(int position);

    RecyclerView.Adapter<ViewHolder> getWrappedAdapter();

    int getAdjustedPosition(int position);
}

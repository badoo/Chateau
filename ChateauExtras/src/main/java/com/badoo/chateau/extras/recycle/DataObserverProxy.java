package com.badoo.chateau.extras.recycle;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

/**
 * User to proxy updates to on Adapter to another adapter, useful when wrapping {@link RecyclerView.Adapter}s
 */
public class DataObserverProxy extends RecyclerView.AdapterDataObserver {

    @NonNull
    private final RecyclerView.Adapter<?> mTo;
    private final Converter mConverter;

    public DataObserverProxy(@NonNull RecyclerView.Adapter<?> to) {
        this(to, new Converter());
    }

    /**
     * @param to        adapter to forward the updates too.
     * @param converter to convert the position to the relevant position in to to adapter.
     */
    public DataObserverProxy(@NonNull RecyclerView.Adapter<?> to, @NonNull Converter converter) {
        mTo = to;
        mConverter = converter;
    }

    @Override
    public void onChanged() {
        mTo.notifyDataSetChanged();
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount) {
        mTo.notifyItemRangeChanged(mConverter.convertPosition(positionStart), itemCount);
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
        mTo.notifyItemRangeChanged(mConverter.convertPosition(positionStart), itemCount, payload);
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
        mTo.notifyItemRangeInserted(mConverter.convertPosition(positionStart), itemCount);
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
        mTo.notifyItemRangeRemoved(mConverter.convertPosition(positionStart), itemCount);
    }

    @Override
    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
        mTo.notifyItemMoved(mConverter.convertPosition(fromPosition), mConverter.convertPosition(toPosition));
    }

    public static class Converter {
        /**
         * Convert position to the position in the adapter been proxied to, by default returns to the position given.
         */
        public int convertPosition(int position) { return position;}
    }
}

package com.badoo.chateau.example.ui.util.recycle;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.badoo.chateau.example.R;


/**
 * Loading adapter which is used to wrap another adapter which can be used ot show and hide a loading spinner at the top of the list.
 */
public class LoadingAdapter<T extends ViewHolder> extends RecyclerView.Adapter<ViewHolder> {

    private final RecyclerView.Adapter<T> mWrappedAdapter;
    private final int mViewTypeId;
    private boolean mLoading = true;

    /**
     * The viewTypeId is the id that should be returned from {@link #getItemViewType(int)} for the loading view type.  This should be one
     * that is not used by the adapter that is been wrapped.
     */
    public LoadingAdapter(@NonNull RecyclerView.Adapter<T> wrappedAdapter, int viewTypeId) {
        mWrappedAdapter = wrappedAdapter;
        mViewTypeId = viewTypeId;

        mWrappedAdapter.registerAdapterDataObserver(new DataObserverProxy(this, new DataObserverProxy.Converter() {
            @Override
            public int convertPosition(int position) {
                return getWrappedAdapterPosition(position);
            }
        }));
    }

    public int getAdjustedPosition(int position) {
        return getWrappedAdapterPosition(position);
    }

    /**
     * Show or hide loading.
     */
    public void setLoading(boolean loading) {
        if (mLoading != loading) {
            mLoading = loading;
            if (mLoading) {
                notifyItemInserted(0);
            }
            else {
                notifyItemRemoved(0);
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == mViewTypeId) {
            return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_loading, parent, false)) {};
        }
        else {
            return mWrappedAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mLoading && position == 0) {
            // Loading spinner
            return;
        }
        //noinspection unchecked
        mWrappedAdapter.onBindViewHolder((T) holder, getWrappedAdapterPosition(position));
    }

    @Override
    public int getItemCount() {
        final int itemCount = mWrappedAdapter.getItemCount();
        return mLoading ? itemCount + 1 : itemCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (mLoading && position == 0) {
            return mViewTypeId;
        }
        return mWrappedAdapter.getItemViewType(getWrappedAdapterPosition(position));
    }

    @Override
    public long getItemId(int position) {
        if (mLoading && position == 0) {
            return Long.MAX_VALUE;
        }
        return mWrappedAdapter.getItemId(getWrappedAdapterPosition(position));
    }

    private int getWrappedAdapterPosition(int position) {
        return mLoading ? position - 1 : position;
    }

}

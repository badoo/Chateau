package com.badoo.chateau.extras.recycle;

import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class BindableViewHolder<T> extends RecyclerView.ViewHolder {

    private T mItem;

    public BindableViewHolder(View itemView) {
        super(itemView);
    }

    @CallSuper
    public void bind(T item) {
        mItem = item;
    }

    public T getBoundItem() {
        return mItem;
    }

}

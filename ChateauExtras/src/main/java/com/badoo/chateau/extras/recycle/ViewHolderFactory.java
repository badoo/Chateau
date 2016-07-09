package com.badoo.chateau.extras.recycle;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public interface ViewHolderFactory<T extends RecyclerView.ViewHolder> {

    @NonNull
    T create(@NonNull ViewGroup parent);
}

package com.badoo.chateau.extras.recycle;

import android.support.annotation.NonNull;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple holder for {@link ViewHolderFactory}s to be used with and {@link android.support.v7.widget.RecyclerView.Adapter}.
 */
public class ViewHolderFactoryResolver<T extends RecyclerView.ViewHolder> {

    private final int mOffset;
    private final Map<Class<?>, ViewHolderFactoryWithId> mViewHolderCreatorsFromType = new HashMap<>();
    private final SparseArrayCompat<ViewHolderFactory<? extends T>> mMessageViewHolderFactories = new SparseArrayCompat<>();

    /**
     * Create with an offset of 0.
     */
    public ViewHolderFactoryResolver() {
        this(0);
    }

    /**
     * Create with a given offset.  The offset is use as the id of the first factory registered with subsequent factories given a and id
     * of offset + number of previous factories registered.
     */
    public ViewHolderFactoryResolver(int offset) {
        mOffset = offset;
    }

    /**
     * Register a factory for a given type, returns the id for that factory.  The id is generated sequentially from offset given in the
     * constructor in the order that the factories are registered (first factories id is offset, second is offset+1 etc).
     */
    public int registerFactory(Class<?> type, ViewHolderFactory<? extends T> factory) {
        final int id = mMessageViewHolderFactories.size() + mOffset;
        mViewHolderCreatorsFromType.put(type, new ViewHolderFactoryWithId(id, factory));
        mMessageViewHolderFactories.put(id, factory);
        return id;
    }

    /**
     * Lookup the id for a given type.
     */
    public int getIdForType(Class<?> type) {
        final ViewHolderFactoryWithId viewHolderCreatorWithId = mViewHolderCreatorsFromType.get(type);
        if (viewHolderCreatorWithId == null) {
            throw new IllegalStateException("No view holder create registered for " + type);
        }
        return viewHolderCreatorWithId.getId();
    }

    /**
     * Retrieve the factory for a given id
     */
    public ViewHolderFactory<? extends T> getFactoryForId(int id) {
        if (id < mOffset || id > mOffset + mMessageViewHolderFactories.size()) {
            throw new IllegalArgumentException("No factory registered for view with id " + id);
        }
        return mMessageViewHolderFactories.get(id);
    }

    private class ViewHolderFactoryWithId implements ViewHolderFactory {
        private int mId;
        private final ViewHolderFactory mFactory;

        ViewHolderFactoryWithId(int id, ViewHolderFactory factory) {
            mId = id;
            mFactory = factory;
        }

        public int getId() {
            return mId;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder create(@NonNull ViewGroup parent) {
            return mFactory.create(parent);
        }
    }
}

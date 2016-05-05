package com.badoo.chateau.example.data.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.badoo.chateau.data.models.BaseUser;

public class ExampleUser extends BaseUser {

    public ExampleUser(@NonNull String userId, @Nullable String displayName) {
        super(userId, displayName);
    }
}

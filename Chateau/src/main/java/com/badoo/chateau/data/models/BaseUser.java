package com.badoo.chateau.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.badoo.chateau.core.model.User;

public class BaseUser implements User, Parcelable {

    private final String mUserId;
    private final String mDisplayName;

    public BaseUser(String userId, String displayName) {
        mUserId = userId;
        mDisplayName = displayName;
    }

    public String getUserId() {
        return mUserId;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mUserId);
        dest.writeString(this.mDisplayName);
    }

    protected BaseUser(Parcel in) {
        this.mUserId = in.readString();
        this.mDisplayName = in.readString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseUser baseUser = (BaseUser) o;

        if (mUserId != null ? !mUserId.equals(baseUser.mUserId) : baseUser.mUserId != null) return false;
        return mDisplayName != null ? mDisplayName.equals(baseUser.mDisplayName) : baseUser.mDisplayName == null;

    }

    @Override
    public int hashCode() {
        int result = mUserId != null ? mUserId.hashCode() : 0;
        result = 31 * result + (mDisplayName != null ? mDisplayName.hashCode() : 0);
        return result;
    }

    public static final Parcelable.Creator<BaseUser> CREATOR = new Parcelable.Creator<BaseUser>() {
        public BaseUser createFromParcel(Parcel source) {
            return new BaseUser(source);
        }

        public BaseUser[] newArray(int size) {
            return new BaseUser[size];
        }
    };
}

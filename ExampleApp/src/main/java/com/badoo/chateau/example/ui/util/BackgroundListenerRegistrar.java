package com.badoo.chateau.example.ui.util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Allows a {@link BackgroundListener} to be registered to keep track of if the application is in the background or foreground.
 */
public class BackgroundListenerRegistrar implements Application.ActivityLifecycleCallbacks {

    private Set<BackgroundListener> mBackgroundListeners = new HashSet<>();

    private boolean mChangingConfig = false;
    private int mForegroundCount = 0;

    /**
     * Register with the application to listener for activities moving in and out of the foreground.  This should be called in
     * {@link Application#onCreate()}.
     */
    public void register(@NonNull Application application) {
        application.registerActivityLifecycleCallbacks(this);
    }

    /**
     * Registers a given listener, if the application is currently in the foreground {@link BackgroundListener#movedToForeground()} will
     * be called immediately.
     */
    public void registerBackgroundListener(@NonNull BackgroundListener listener) {
        mBackgroundListeners.add(listener);
        if (mForegroundCount > 0) {
            listener.movedToForeground();
        }
    }

    /**
     * Unregistered a previously register listener.
     */
    public void unregisterBackgroundListener(@NonNull BackgroundListener listener) {
        mBackgroundListeners.remove(listener);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (!mChangingConfig) {
            mForegroundCount++;
            // If we have a foreground count of 1 that means we've moved from the background to the foreground
            if (mForegroundCount == 1) {
                for (BackgroundListener backgroundListener : mBackgroundListeners) {
                    backgroundListener.movedToForeground();
                }
            }
        }
        mChangingConfig = false;
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (!activity.isChangingConfigurations()) {
            mForegroundCount--;
        }
        else {
            mChangingConfig = true;
        }
        // If we have a foreground count of 0 that means we've moved from the foreground to the background
        if (mForegroundCount == 0) {
            for (BackgroundListener backgroundListener : mBackgroundListeners) {
                backgroundListener.movedToBackground();
            }
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    /**
     * Listener to keep track of if the application is currently in the background or foreground.
     */
    public interface BackgroundListener {

        /**
         * Called when the application moves into the foreground.
         */
        void movedToForeground();

        /**
         * Called when the application moves into the background.
         */
        void movedToBackground();

    }

}

package com.badoo.chateau.example;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.badoo.chateau.example.ui.BaseActivity;

import org.junit.Rule;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public abstract class BaseTestCase<A extends BaseActivity> {

    @Rule
    public ActivityTestRule<A> mActivityRule = new ActivityTestRule<A>(
        getActivityClass()) {

        @Override
        protected void beforeActivityLaunched() {
            super.beforeActivityLaunched();
            BaseTestCase.this.beforeActivityLaunched();
        }

        @Override
        protected Intent getActivityIntent() {
            return BaseTestCase.this.getActivityIntent();
        }
    };

    protected Intent getActivityIntent() {
        return null;
    }

    protected abstract Class<A> getActivityClass();

    protected abstract void beforeActivityLaunched();

    protected void runOnUiThread(@NonNull Runnable runnable) {
        mActivityRule.getActivity().runOnUiThread(runnable);
    }

    @SuppressWarnings("unused")
    public abstract static class SimpleAnswer implements Answer {

        @Override
        public final Object answer(InvocationOnMock invocation) throws Throwable {
            answer();
            return null;
        }

        protected abstract void answer();
    }

    @SuppressWarnings("unused")
    public static class WaitForViewIdlingResource<A extends BaseActivity> implements IdlingResource {

        private final View mView;
        private final int mVisibility;
        private final String mName;
        private ResourceCallback mCallback;

        public WaitForViewIdlingResource(@IdRes int viewId, int visibility, @NonNull ActivityTestRule<A> rule) {
            mName = "waitForView-" + viewId;
            mView = rule.getActivity().findViewById(viewId);
            mVisibility = visibility;
        }

        @Override
        public String getName() {
            return mName;
        }

        @Override
        public boolean isIdleNow() {
            if (mView.getVisibility() == mVisibility) {
                if (mCallback != null) {
                    mCallback.onTransitionToIdle();
                }
                return true;
            }
            return false;
        }

        @Override
        public void registerIdleTransitionCallback(ResourceCallback callback) {
            mCallback = callback;
        }
    }

    public static class WaitForRecycleViewScrollIdlingResource<A extends BaseActivity> implements IdlingResource {
        private final RecyclerView mView;
        private final int mPosition;
        private final String mName;
        private ResourceCallback mCallback;

        public WaitForRecycleViewScrollIdlingResource(@IdRes int viewId, int position, @NonNull ActivityTestRule<A> rule) {
            mName = "waitForView-" + viewId;
            mView = (RecyclerView) rule.getActivity().findViewById(viewId);
            mPosition = position;
        }

        @Override
        public String getName() {
            return mName;
        }

        @Override
        public boolean isIdleNow() {
            if (((LinearLayoutManager)mView.getLayoutManager()).findFirstCompletelyVisibleItemPosition() == mPosition) {
                if (mCallback != null) {
                    mCallback.onTransitionToIdle();
                }
                return true;
            }
            return false;
        }

        @Override
        public void registerIdleTransitionCallback(ResourceCallback callback) {
            mCallback = callback;
        }
    }
}

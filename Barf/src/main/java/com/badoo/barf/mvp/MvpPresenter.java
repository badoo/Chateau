package com.badoo.barf.mvp;

import android.app.Activity;
import android.app.Fragment;

/**
 * Interface definition of a MVP Presenter.
 */
public interface MvpPresenter {

    /**
     * Lifecycle callback that is invoked from onStart of the Fragment/Activity managing the presenter
     */
    void onStart();

    /**
     * Lifecycle callback that is invoked from onStop of the Fragment/Activity managing the presenter
     */
    void onStop();

}

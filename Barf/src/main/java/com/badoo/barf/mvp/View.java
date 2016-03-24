package com.badoo.barf.mvp;

import android.support.annotation.NonNull;

/**
 * Interface definition of a MVP View. Contains base methods in common for all views.
 */
public interface View<P extends Presenter> {

    /**
     * Attach a Presenter to this View so that the view can notify it about user actions.
     */
    void attachPresenter(@NonNull P presenter);
}

package com.badoo.barf.mvp;

import android.app.Activity;
import android.app.Fragment;
import android.support.annotation.NonNull;

/**
 * Interface definition of a MVP Presenter. Contains base functionality for integration with View and FlowListener (optional)
 * as well as lifecycle callbacks.
 */
public interface Presenter<V extends View, F extends Presenter.FlowListener> {

    /**
     * Attach the view to this presenter, should be called directly after the presenter is constructed.
     */
    void attachView(@NonNull V view);

    /**
     * @param flowListener a listener used by the presenter to notify it's parent that it's reached it's end state.
     *                     For example if the presenter is used to select a user, at the point the user is selected,
     *                     there is nothing else the presenter can do, so at this point it would notify it's parent
     *                     that a user has been selected.
     */
    void attachFlowListener(@NonNull F flowListener);

    /**
     * Lifecycle callback that is invoked when the presenter is created
     */
    void onCreate();

    /**
     * Lifecycle callback that is invoked from onStart of the Fragment/Activity managing the presenter
     */
    void onStart();

    /**
     * Lifecycle callback that is invoked from onStop of the Fragment/Activity managing the presenter
     */
    void onStop();

    /**
     * Should be called by either {@link Activity#onDestroy()}, {@link Fragment#onDestroy()} or {@link Fragment#onDestroyView()}
     */
    void destroy();


    /**
     * An interface that is used in addition to the View for allowing the presenter to control navigation related functionality of the app.
     * This was introduced to solve issues that arose from separating the View implementation from the containing Fragment/Activity as well
     * as from the decision to split functionality into several View/Presenter pairs for each screen (based on areas or responsibility).
     */
    interface FlowListener {

        /**
         * A class that be used if no flow listener is required for the presenter.
         */
        class NoFlowListener implements FlowListener {
        }
    }
}

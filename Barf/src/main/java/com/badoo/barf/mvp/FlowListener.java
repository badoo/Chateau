package com.badoo.barf.mvp;

/**
 * An marker interface that is used in addition to the View for allowing the presenter to control navigation related functionality of the app.
 * This was introduced to solve issues that arose from separating the View implementation from the containing Fragment/Activity as well
 * as from the decision to split functionality into several View/Presenter pairs for each screen (based on areas or responsibility).
 */
public interface FlowListener {
}

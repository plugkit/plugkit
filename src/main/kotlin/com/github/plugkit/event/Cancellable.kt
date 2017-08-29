package com.github.plugkit.event

/**
 * Events can implement this interface if they should be able to be prevented or reversed.
 */
interface Cancellable {
    /**
     * The cancellation state of this event.
     *
     * Cancellable events are fired either before the event actually happens or rolls back what happened if the event
     * is cancelled. A cancelled event will still pass to other registered listeners.
     */
    var isCancelled: Boolean
}
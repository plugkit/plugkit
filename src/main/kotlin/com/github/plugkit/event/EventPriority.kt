package com.github.plugkit.event

/**
 * Represents an event's priority in execution
 */
enum class EventPriority private constructor(val slot: Int) {

    /**
     * Event call is of very low importance and should be ran first, to allow
     * other plugins to further customise the outcome
     */
    LOWEST(0),
    /**
     * Event call is of low importance
     */
    LOW(1),
    /**
     * Event call is neither important nor unimportant, and may be ran
     * normally
     */
    NORMAL(2),
    /**
     * Event call is of high importance
     */
    HIGH(3),
    /**
     * Event call is critical and must have the final say in what happens
     * to the event
     */
    HIGHEST(4),
    /**
     * Event is listened to purely for monitoring the outcome of an event.
     *
     * No modifications to the event should be made under this priority
     */
    MONITOR(5)
}

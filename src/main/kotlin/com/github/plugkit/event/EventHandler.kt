package com.github.plugkit.event

/**
 * An annotation to mark methods as being event handler methods
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class EventHandler(
        /**
         * Defines the priority of the event.
         *
         * First priority to the last priority executed:
         *
         *  1. LOWEST
         *  1. LOW
         *  1. NORMAL
         *  1. HIGH
         *  1. HIGHEST
         *  1. MONITOR
         *
         * @return the priority
         */
        val priority: EventPriority = EventPriority.NORMAL,
        /**
         * Defines if the handler ignores a cancelled event.
         *
         * If ignoreCancelled is true and the event is cancelled, the method is
         * not called. Otherwise, the method is always called.
         *
         * @return whether cancelled events should be ignored
         */
        val ignoreCancelled: Boolean = false)

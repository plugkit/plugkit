package com.github.plugkit.plugin

import com.github.plugkit.PluginHost
import com.github.plugkit.event.*

/**
 * Stores relevant information for plugin listeners
 */
class RegisteredListener<H : PluginHost>(
        /**
         * Gets the listener for this registration
         *
         * @return Registered Listener
         */
        val listener: Listener, private val executor: EventExecutor<H>,
        /**
         * Gets the priority for this registration
         *
         * @return Registered Priority
         */
        val priority: EventPriority,
        /**
         * Gets the plugin for this registration
         *
         * @return Registered Plugin
         */
        val plugin: Plugin<H>,
        /**
         * Whether this listener accepts cancelled events
         *
         * @return True when ignoring cancelled events
         */
        val isIgnoringCancelled: Boolean) {

    /**
     * Calls the event executor
     *
     * @param event The event
     * @throws EventException If an event handler throws an exception.
     */
    @Throws(EventException::class)
    fun callEvent(event: Event<H>) {
        if (event is Cancellable) {
            if ((event as Cancellable).isCancelled && isIgnoringCancelled) {
                return
            }
        }
        executor.execute(listener, event)
    }
}

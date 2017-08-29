package com.github.plugkit.event

import com.github.plugkit.PluginHost

/**
 * Represents an event.
 *
 * All events require a static method named getHandlerList() which returns the same [HandlerList] as [.getHandlers].
 *
 * @see PluginManager.callEvent
 * @see PluginManager.registerEvents
 */
abstract class Event<H : PluginHost> {
    /**
     * A user-friendly identifier.
     *
     * By default, it is the event's [Class.getSimpleName].
     */
    open val eventName: String by lazy { this::class.java.simpleName }

    abstract val handlers: HandlerList<H>

    enum class Result {
        /**
         * Deny the event. Depending on the event, the action indicated by the
         * event will either not take place or will be reverted. Some actions
         * may not be denied.
         */
        DENY,
        /**
         * Neither deny nor allow the event. The server will proceed with its
         * normal handling.
         */
        DEFAULT,
        /**
         * Allow / Force the event. The action indicated by the event will
         * take place if possible, even if the server would not normally allow
         * the action. Some actions may not be allowed.
         */
        ALLOW
    }
}
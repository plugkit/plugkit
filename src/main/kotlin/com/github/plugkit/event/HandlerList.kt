package com.github.plugkit.event

import com.github.plugkit.PluginHost
import com.github.plugkit.plugin.Plugin
import com.github.plugkit.plugin.RegisteredListener
import java.util.ArrayList
import java.util.EnumMap

/**
 * A list of event handlers, stored per-event. Based on lahwran's fevents.
 */
open class HandlerList<H : PluginHost> {

    /**
     * Handler array. This field being an array is the key to this system's speed.
     */
    @Volatile private var handlers: Array<RegisteredListener<H>>? = null

    private fun clearHandlers() {
        handlers = null
    }

    /**
     * Dynamic handler lists. These are changed using register() and unregister() and are automatically baked to the
     * handlers array any time they have changed.
     */
    private val handlerslots: EnumMap<EventPriority, ArrayList<RegisteredListener<H>>>

    private operator fun EnumMap<EventPriority, ArrayList<RegisteredListener<H>>>.invoke(priority: EventPriority):
            ArrayList<RegisteredListener<H>> {
        return this[priority] ?:
                throw IllegalStateException("handlerslots was not initialized")
    }

    /**
     * Create a new handler list and initialize using EventPriority.
     *
     * The HandlerList is then added to meta-list for use in bakeAll()
     */
    init {
        handlerslots = EnumMap<EventPriority, ArrayList<RegisteredListener<H>>>(EventPriority::class.java)
        for (o in EventPriority.values()) {
            handlerslots.put(o, ArrayList<RegisteredListener<H>>())
        }
        synchronized(allLists) {
            allLists.add(this)
        }
    }

    /**
     * Register a new listener in this handler list
     *
     * @param listener listener to register
     */
    @Synchronized
    fun register(listener: RegisteredListener<H>) {
        if (handlerslots(listener.priority).contains(listener)) {
            throw IllegalStateException("This listener is already registered to priority ${listener.priority}")
        }
        handlers = null
        handlerslots[listener.priority]?.add(listener)
    }

    /**
     * Register a collection of new listeners in this handler list
     *
     * @param listeners listeners to register
     */
    fun registerAll(listeners: Collection<RegisteredListener<H>>) {
        for (listener in listeners) {
            register(listener)
        }
    }

    /**
     * Remove a listener from a specific order slot
     *
     * @param listener listener to remove
     */
    @Synchronized
    fun unregister(listener: RegisteredListener<H>) {
        if (handlerslots(listener.priority).remove(listener)) {
            handlers = null
        }
    }

    /**
     * Remove a specific plugin's listeners from this handler
     *
     * @param plugin plugin to remove
     */
    @Synchronized
    fun unregisterPlugin(plugin: Plugin<*>) {
        var changed = false
        for (list in handlerslots.values) {
            val i = list.listIterator()
            while (i.hasNext()) {
                if (i.next().plugin.equals(plugin)) {
                    i.remove()
                    changed = true
                }
            }
        }
        if (changed) handlers = null
    }

    /**
     * Remove a specific listener from this handler
     *
     * @param listener listener to remove
     */
    @Synchronized
    fun unregister(listener: Listener) {
        var changed = false
        for (list in handlerslots.values) {
            val i = list.listIterator()
            while (i.hasNext()) {
                if (i.next().listener.equals(listener)) {
                    i.remove()
                    changed = true
                }
            }
        }
        if (changed) handlers = null
    }

    /**
     * Bake HashMap and ArrayLists to 2d array - does nothing if not necessary
     */
    @Synchronized
    fun bake() {
        if (handlers != null) return  // don't re-bake when still valid
        val entries = ArrayList<RegisteredListener<H>>()
        for ((_, value) in handlerslots) {
            entries.addAll(value)
        }
        handlers = entries.toTypedArray()
    }

    /**
     * Get the baked registered listeners associated with this handler list
     *
     * @return the array of registered listeners
     */
    // This prevents fringe cases of returning null
    val registeredListeners: Array<RegisteredListener<H>>
        get() {
            var handlers: Array<RegisteredListener<H>>? = this.handlers
            while (handlers == null) {
                bake()
                handlers = this.handlers
            }
            return handlers
        }

    companion object {

        /**
         * List of all HandlerLists which have been created, for use in bakeAll()
         */
        private val allLists = ArrayList<HandlerList<*>>()

        /**
         * Bake all handler lists. Best used just after all normal event
         * registration is complete, ie just after all plugins are loaded if
         * you're using fevents in a plugin system.
         */
        @JvmStatic
        fun bakeAll() {
            synchronized(allLists) {
                for (h in allLists) {
                    h.bake()
                }
            }
        }

        /**
         * Unregister all listeners from all handler lists.
         */
        @JvmStatic
        fun unregisterAll() {
            synchronized(allLists) {
                for (h in allLists) {
                    synchronized(h) {
                        for (list in h.handlerslots.values) {
                            list.clear()
                        }
                        h.clearHandlers()
                    }
                }
            }
        }

        /**
         * Unregister a specific plugin's listeners from all handler lists.
         *
         * @param plugin plugin to unregister
         */
        @JvmStatic
        fun unregisterAll(plugin: Plugin<*>) {
            synchronized(allLists) {
                for (h in allLists) {
                    h.unregisterPlugin(plugin)
                }
            }
        }

        /**
         * Unregister a specific listener from all handler lists.
         *
         * @param listener listener to unregister
         */
        @JvmStatic
        fun unregisterAll(listener: Listener) {
            synchronized(allLists) {
                for (h in allLists) {
                    h.unregister(listener)
                }
            }
        }

        /**
         * Get a specific plugin's registered listeners associated with this
         * handler list
         *
         * @param plugin the plugin to get the listeners of
         * @return the list of registered listeners
         */
        @JvmStatic
        fun getRegisteredListeners(plugin: Plugin<*>): ArrayList<RegisteredListener<*>> {
            val listeners = ArrayList<RegisteredListener<*>>()
            synchronized(allLists) {
                for (h in allLists) {
                    synchronized(h) {
                        for (list in h.handlerslots.values) {
                            for (listener in list) {
                                if (listener.plugin.equals(plugin)) {
                                    listeners.add(listener)
                                }
                            }
                        }
                    }
                }
            }
            return listeners
        }

        /**
         * Get a list of all handler lists for every event type
         *
         * @return the list of all handler lists
         */
        @JvmStatic
        val handlerLists: ArrayList<HandlerList<*>>
            get() = synchronized(allLists) {
                return allLists.clone() as ArrayList<HandlerList<*>>
            }
    }
}
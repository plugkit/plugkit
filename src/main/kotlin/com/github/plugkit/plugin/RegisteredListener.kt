/*
 * This file is part of Plugkit.
 *
 * Copyright (C) 2010-2014  Bukkit Team
 * Copyright (C) 2014-2017  Spigot Team
 * Copyright (C) 2017  Plugkit Team
 *
 * Plugkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Plugkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Plugkit.  If not, see <http://www.gnu.org/licenses/>.
 */
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

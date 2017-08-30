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
import com.github.plugkit.event.Event
import com.github.plugkit.event.Listener
import java.io.File

interface PluginLoader<H : PluginHost> {

    /**
     * Loads the plugin contained in the specified file.
     *
     * @param file File to attempt to load.
     * @return Plugin that was contained in the specified file, or null if unsuccessful.
     * @throws InvalidPluginException Thrown when the specified file is not a plugin.
     */
    @Throws(InvalidPluginException::class)
    fun loadPlugin(file: File): Plugin<H>

    /**
     * Loads a PluginDescription from the specified file.
     *
     * @param file File to attempt to load from.
     * @return A new PluginDescription loaded from the plugin.yml in the specified file.
     */
    @Throws(InvalidDescriptionException::class)
    fun getPluginDescription(file: File): PluginDescription

    /**
     * A list of the file filters that describe what type of files this plugin loader will load as plugins.
     */
    val pluginFileFilters: List<Regex>

    /**
     * Enables the specified plugin.
     *
     * Attempting to enable a plugin that is already enabled will have no effect.
     *
     * @param plugin Plugin to enable.
     */
    fun enablePlugin(plugin: Plugin<H>)

    /**
     * Disables the specified plugin.
     *
     * Attempting to disable a plugin that is not enabled will have no effect.
     *
     * @param plugin Plugin to disable.
     */
    fun disablePlugin(plugin: Plugin<H>)

    /**
     * Creates and returns registered listeners for the event classes used in
     * this listener
     *
     * @param listener The object that will handle the eventual call back
     * @param plugin The plugin to use when creating registered listeners
     * @return The registered listeners.
     */
    fun createRegisteredListeners(listener: Listener, plugin: Plugin<H>): Map<Class<out Event<H>>, Set<RegisteredListener<H>>>
}
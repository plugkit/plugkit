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
package com.github.plugkit.plugin.java

import com.github.plugkit.PluginHost
import com.github.plugkit.plugin.Plugin
import com.github.plugkit.plugin.PluginDescription
import com.github.plugkit.plugin.PluginLoader
import dtmlibs.logging.Logging
import mu.KLogger

abstract class JavaPlugin<H : PluginHost> : Plugin<H> {

    override final lateinit var host: H
        internal set

    override final lateinit var description: PluginDescription
        internal set

    override lateinit var logger: KLogger
        internal set

    internal var enabled = false
        set(value) {
            field = value
            if (value) onEnable() else onDisable()
        }
    override final val isEnabled
        get() = enabled
    override final val isNotEnabled
        get() = !isEnabled


    val classLoader = this::class.java.classLoader

    init {
        classLoader as PluginClassLoader<H>
        classLoader.initialize(this)
    }

    override final lateinit var pluginLoader: PluginLoader<H>
        internal set

    override fun onEnable() { }

    override fun onDisable() { }

    internal fun init(host: H, loader: PluginLoader<H>, description: PluginDescription) {
        this.host = host
        this.pluginLoader = loader
        this.description = description
        Logging.registerLogOwner(this::class.java, this.description.name)
        this.logger = Logging.getLogger(this)
    }
}
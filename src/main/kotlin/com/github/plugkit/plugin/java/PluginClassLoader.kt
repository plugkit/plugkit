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
import com.github.plugkit.plugin.InvalidPluginException
import com.github.plugkit.plugin.Plugin
import com.github.plugkit.plugin.PluginDescription
import java.io.File
import java.net.URLClassLoader
import java.util.concurrent.ConcurrentHashMap

internal class PluginClassLoader<H : PluginHost>(private val loader: JavaPluginLoader<H>,
                                                 parent: ClassLoader,
                                                 private val description: PluginDescription,
                                                 dataFolder: File,
                                                 file: File)
    : URLClassLoader(arrayOf(file.toURI().toURL()), parent) {

    val plugin: Plugin<H>

    private var pluginInit: JavaPlugin<H>? = null
    private var pluginState: IllegalStateException? = null
    private val classMap: MutableMap<String, Class<*>?> = ConcurrentHashMap()
    val classes: Set<String>
        get() = classMap.keys

    init {
        try {
            val jarClass: Class<*>
            try {
                jarClass = Class.forName(description.main, true, this)
            } catch (e: ClassNotFoundException) {
                throw InvalidPluginException("Cannot find main class '${description.main}'", e)
            }

            val pluginClass: Class<out JavaPlugin<H>>
            try {
                pluginClass = jarClass.asSubclass(JavaPlugin::class.java) as Class<out JavaPlugin<H>>
            } catch (e: ClassCastException) {
                throw InvalidPluginException("main class '${description.main}' does not extend JavaPlugin", e)
            }


            plugin = pluginClass.newInstance()
        } catch (ex: IllegalAccessException) {
            throw InvalidPluginException("No public constructor", ex)
        } catch (ex: InstantiationException) {
            throw InvalidPluginException("Abnormal plugin type", ex)
        }
    }

    @Throws(ClassNotFoundException::class)
    override fun findClass(name: String): Class<*> {
        return findClass(name, true) ?: throw ClassNotFoundException(name)
    }

    @Throws(ClassNotFoundException::class)
    internal fun findClass(name: String, checkGlobal: Boolean): Class<*>? {
        if (name.startsWith("kaddy.")) {
            throw ClassNotFoundException(name)
        }
        var result: Class<*>? = classMap[name]

        if (result == null) {
            if (checkGlobal) {
                result = loader.getClassByName(name)
            }

            if (result == null) {
                result = super.findClass(name)

                if (result != null) {
                    loader.setClass(name, result)
                }
            }

            classMap.put(name, result)
        }

        return result
    }

    @Synchronized
    internal fun initialize(plugin: JavaPlugin<H>) {
        if (plugin::class.java.classLoader !== this)
            throw IllegalArgumentException("Cannot initialize plugin outside of this class loader")
        if (this.pluginInit != null) {
            throw IllegalArgumentException("Plugin already initialized!", pluginState)
        }

        pluginState = IllegalStateException("Initial initialization")
        this.pluginInit = plugin

        plugin.init(this.loader.host, loader, description)
    }
}
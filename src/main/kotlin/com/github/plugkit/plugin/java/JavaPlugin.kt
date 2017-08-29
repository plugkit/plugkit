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
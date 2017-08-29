package com.github.plugkit.plugin

import com.github.plugkit.PluginHost
import dtmlibs.logging.LogOwner

interface Plugin<H : PluginHost> : LogOwner {

    val host: H
    val description: PluginDescription
    val pluginLoader: PluginLoader<H>

    val isEnabled: Boolean
    val isNotEnabled
        get() = !isEnabled

    fun onEnable()
    fun onDisable()
}
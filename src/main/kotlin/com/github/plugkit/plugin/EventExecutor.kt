package com.github.plugkit.plugin

import com.github.plugkit.PluginHost
import com.github.plugkit.event.Event
import com.github.plugkit.event.EventException
import com.github.plugkit.event.Listener

/**
 * Interface which defines the class for event call backs to plugins
 */
interface EventExecutor<H : PluginHost> {
    @Throws(EventException::class)
    fun execute(listener: Listener, event: Event<H>)
}
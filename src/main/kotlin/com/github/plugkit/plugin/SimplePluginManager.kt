package com.github.plugkit.plugin

import com.github.plugkit.PluginHost
import com.github.plugkit.event.Event
import com.github.plugkit.event.HandlerList
import com.github.plugkit.event.Listener
import com.github.plugkit.util.FileUtil
import dtmlibs.logging.LogOwner
import dtmlibs.logging.Loggable
import java.io.File
import java.io.IOException
import java.lang.reflect.Constructor

class SimplePluginManager<H : PluginHost>(private val host: H, private val hostType: Class<out H>)
    : PluginManager<H>, Loggable {

    override val logOwner: Class<out LogOwner> = hostType

    private val updateDirectory = File("./updates")
    private val fileAssociations = mutableMapOf<Regex, PluginLoader<H>>()
    private val plugins = mutableListOf<Plugin<H>>()
    private val lookupNames = mutableMapOf<String, Plugin<H>>()

    override fun registerInterface(loader: Class<out PluginLoader<H>>) {
        val instance: PluginLoader<H>

        if (PluginLoader::class.java.isAssignableFrom(loader)) {
            val constructor: Constructor<out PluginLoader<H>>

            try {
                constructor = loader.getDeclaredConstructor(hostType)
                constructor.isAccessible = true
                instance = constructor.newInstance(host)
            } catch (e: NoSuchMethodException) {
                val className = loader.name
                throw IllegalArgumentException("Class $className does not have a $className(Kaddy) constructor", e)
            } catch (e: Exception) {
                throw IllegalArgumentException("Unexpected exception ${e::class.java.name} while attempting to" +
                        " construct a new instance of ${loader.name}", e)
            }

        } else {
            throw IllegalArgumentException(String.format("Class %s does not implement interface PluginLoader", loader.name))
        }

        val patterns = instance.pluginFileFilters

        synchronized(this) {
            for (pattern in patterns) {
                fileAssociations.put(pattern, instance)
            }
        }
    }

    private fun checkUpdate(file: File) {
        if (!updateDirectory.isDirectory()) {
            return
        }

        val updateFile = File(updateDirectory, file.name)
        if (updateFile.isFile()) {
            try {
                FileUtil.copy(updateFile, file)
                updateFile.delete()
            } catch (ignore: IOException) { }
        }
    }

    override fun loadPlugin(file: File): Plugin<H>? {
        checkUpdate(file)

        val filters = fileAssociations.keys
        var result: Plugin<H>? = null

        logger.info { filters }

        for (filter in filters) {
            val name = file.name

            logger.info { "$filter matches $name? ${filter.matches(name)}"}

            if (filter.matches(name)) {
                val loader = fileAssociations[filter]

                result = loader?.loadPlugin(file) ?: throw IllegalStateException("Unexpected missing PluginLoader")
            }
        }

        if (result != null) {
            plugins.add(result)
            lookupNames[result.description.name] = result
        }

        return result
    }

    override fun unloadPlugin(plugin: Plugin<H>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun enablePlugin(plugin: Plugin<H>) {
        if (!plugin.isEnabled) {
            try {
                plugin.pluginLoader.enablePlugin(plugin)
            } catch (e: Throwable) {
                logger.error(e) { "Error occurred (in the plugin loader) while enabling " +
                        "${plugin.description.fullName} (Is it up to date?)" }
            }
        }
    }

    override fun disablePlugin(plugin: Plugin<H>) {
        if (plugin.isEnabled) {
            try {
                plugin.pluginLoader.disablePlugin(plugin)
            } catch (e: Throwable) {
                logger.error(e) { "Error occurred (in the plugin loader) while disabling " +
                        "${plugin.description.fullName} (Is it up to date?)" }
            }

            // TODO unregister listeners
        }
    }

    override fun callEvent(event: Event<H>) {
        fireEvent(event)
    }

    private fun fireEvent(event: Event<H>) {
        val handlers = event.handlers
        val listeners = handlers.registeredListeners

        for (registration in listeners) {
            if (registration.plugin.isNotEnabled) {
                continue
            }

            try {
                registration.callEvent(event)
            }
//            catch (e: AuthorNagException) {
//                val plugin = registration.plugin
//
//                if (plugin.isNaggable()) {
//                    plugin.setNaggable(false)
//
//                    server.getLogger().log(Level.SEVERE, String.format(
//                            "Nag author(s): '%s' of '%s' about the following: %s",
//                            plugin.description.authors,
//                            plugin.getDescription().getFullName(),
//                            e.getMessage()
//                    ))
//                }
//            }
            catch (e: Throwable) {
                logger.error(e) { "Could not pass event ${event.eventName} to " +
                        registration.plugin.description.fullName }
            }

        }
    }

    override fun registerEvents(listener: Listener, plugin: Plugin<H>) {
        if (plugin.isNotEnabled) {
            throw IllegalPluginAccessException("Plugin attempted to register $listener while not enabled")
        }

        for (entry in plugin.pluginLoader.createRegisteredListeners(listener, plugin).entries) {
            getEventListeners(getRegistrationClass(entry.key)).registerAll(entry.value)
        }
    }

    private fun getEventListeners(type: Class<out Event<H>>): HandlerList<H> {
        try {
            val method = getRegistrationClass(type).getDeclaredMethod("getHandlerList")
            method.isAccessible = true
            return method.invoke(null) as HandlerList<H>
        } catch (e: Exception) {
            throw IllegalPluginAccessException(e.toString())
        }

    }

    private fun getRegistrationClass(clazz: Class<out Event<H>>): Class<out Event<H>> {
        try {
            clazz.getDeclaredMethod("getHandlerList")
            return clazz
        } catch (e: NoSuchMethodException) {
            return if (clazz.superclass != null
                    && clazz.superclass != Event::class.java
                    && Event::class.java.isAssignableFrom(clazz.superclass)) {
                getRegistrationClass(clazz.superclass.asSubclass(Event::class.java) as Class<out Event<H>>)
            } else {
                throw IllegalPluginAccessException("Unable to find handler list for event ${clazz.name}. Static" +
                        " getHandlerList method required!")
            }
        }

    }
}
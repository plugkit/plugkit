package com.github.plugkit.plugin

import com.typesafe.config.Config

class PluginDescriptionConfig(config: Config) : PluginDescription(config.getString("name"),
        config.getString("version"), config.getString("main"), config.getString("authors"))
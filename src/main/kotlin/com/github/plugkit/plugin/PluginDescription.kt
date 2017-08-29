package com.github.plugkit.plugin

open class PluginDescription(val name: String, val version: String, val main: String, val authors: String) {

    val fullName
        get() = "$name v$version"

    init {
        if (!NAME_PATTERN.toRegex().matches(name)) {
            throw InvalidDescriptionException("Name $name contains invalid characters")
        }
    }

    companion object {
        const val NAME_PATTERN = """^[A-Za-z0-9_.-]+$"""
    }
}
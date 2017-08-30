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
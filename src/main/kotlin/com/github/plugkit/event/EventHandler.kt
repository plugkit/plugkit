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
package com.github.plugkit.event

/**
 * An annotation to mark methods as being event handler methods
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class EventHandler(
        /**
         * Defines the priority of the event.
         *
         * First priority to the last priority executed:
         *
         *  1. LOWEST
         *  1. LOW
         *  1. NORMAL
         *  1. HIGH
         *  1. HIGHEST
         *  1. MONITOR
         *
         * @return the priority
         */
        val priority: EventPriority = EventPriority.NORMAL,
        /**
         * Defines if the handler ignores a cancelled event.
         *
         * If ignoreCancelled is true and the event is cancelled, the method is
         * not called. Otherwise, the method is always called.
         *
         * @return whether cancelled events should be ignored
         */
        val ignoreCancelled: Boolean = false)

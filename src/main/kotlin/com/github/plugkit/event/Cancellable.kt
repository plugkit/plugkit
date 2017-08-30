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
 * Events can implement this interface if they should be able to be prevented or reversed.
 */
interface Cancellable {
    /**
     * The cancellation state of this event.
     *
     * Cancellable events are fired either before the event actually happens or rolls back what happened if the event
     * is cancelled. A cancelled event will still pass to other registered listeners.
     */
    var isCancelled: Boolean
}
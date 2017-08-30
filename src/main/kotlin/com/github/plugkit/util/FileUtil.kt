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
package com.github.plugkit.util

import java.nio.channels.FileChannel
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

/**
 * Class containing file utilities
 */
object FileUtil {

    /**
     * This method copies one file to another location
     *
     * @param inFile the source filename
     * @param outFile the target filename
     * @return true on success
     */
    fun copy(inFile: File, outFile: File): Boolean {
        if (!inFile.exists()) {
            return false
        }

        var `in`: FileChannel? = null
        var out: FileChannel? = null

        try {
            `in` = FileInputStream(inFile).channel
            out = FileOutputStream(outFile).channel

            var pos: Long = 0
            val size = `in`!!.size()

            while (pos < size) {
                pos += `in`.transferTo(pos, (10 * 1024 * 1024).toLong(), out)
            }
        } catch (ioe: IOException) {
            return false
        } finally {
            try {
                if (`in` != null) {
                    `in`.close()
                }
                if (out != null) {
                    out.close()
                }
            } catch (ioe: IOException) {
                return false
            }

        }

        return true

    }
}
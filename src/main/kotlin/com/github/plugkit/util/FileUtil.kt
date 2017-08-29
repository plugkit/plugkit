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
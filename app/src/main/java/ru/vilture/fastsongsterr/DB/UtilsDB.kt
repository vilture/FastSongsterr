package ru.vilture.fastsongsterr.DB

import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel

object UtilsDB {
    /**
     * Creates the specified `toFile` as a byte for byte copy of the
     * `fromFile`. If `toFile` already exists, then it
     * will be replaced with a copy of `fromFile`. The name and path
     * of `toFile` will be that of `toFile`.<br></br>
     * <br></br>
     * * Note: `fromFile` and `toFile` will be closed by
     * this function.*
     *
     * @param fromFile
     * - FileInputStream for the file to copy from.
     * @param toFile
     * - FileInputStream for the file to copy to.
     */
    @Throws(IOException::class)
    fun copyFile(fromFile: FileInputStream, toFile: FileOutputStream) {
        var fromChannel: FileChannel? = null
        var toChannel: FileChannel? = null
        try {
            fromChannel = fromFile.channel
            toChannel = toFile.channel
            fromChannel.transferTo(0, fromChannel.size(), toChannel)
        } finally {
            try {
                fromChannel?.close()
            } finally {
                toChannel?.close()
            }
        }
    }
}
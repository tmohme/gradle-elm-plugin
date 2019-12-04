package org.mohme.gradle

import com.github.kittinunf.result.Result
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import java.util.zip.GZIPInputStream

fun File.isGzipped() = name.toLowerCase(Locale.US).endsWith(".gz")

fun File.unGzip(target: File): File {
    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)

    // TODO handle different compression formats!?
    GZIPInputStream(FileInputStream(this)).use { gzis ->
        FileOutputStream(target).use { fos ->
            var len: Int
            while (gzis.read(buffer).also { len = it } > 0) {
                fos.write(buffer, 0, len)
            }
        }
    }

    return target
}

object Unpacker {
    fun unpack(packed: File): Result<File, Exception> =
            Result.of {
                if (packed.isGzipped()) {
                    // TODO make naming variable!?
                    packed.unGzip(target = packed.resolveSibling("elm"))
                } else {
                    throw IllegalArgumentException("Unable to unpack '$packed'.")
                }
            }
}

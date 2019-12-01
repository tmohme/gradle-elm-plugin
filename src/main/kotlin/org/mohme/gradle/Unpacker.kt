package org.mohme.gradle

import com.github.kittinunf.result.Result
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import java.util.zip.GZIPInputStream

fun File.isGzipped() = name.toLowerCase(Locale.US).endsWith(".gz")

fun File.unGzip(): File {
    val buffer = ByteArray(1024 * 128)

    // TODO handle different compression formats!?
    // TODO make naming variable!?
    val targetFile = this.resolveSibling("elm")

    GZIPInputStream(FileInputStream(this)).use { gzis ->
        FileOutputStream(targetFile).use { fos ->

            var len: Int
            while (gzis.read(buffer).also { len = it } > 0) {
                fos.write(buffer, 0, len)
            }

        }
    }

    return targetFile
}

object Unpacker {
    fun unpack(packed: File): Result<File, Exception> =
            Result.of {
                if (packed.isGzipped()) {
                    packed.unGzip()
                } else {
                    throw IllegalArgumentException("Unable to unpack '$packed'.")
                }
            }
}
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

class Unpacker(private val logger: Logger) {

    sealed class UnpackResult {
        abstract val file: File
        data class NewFile(override val file : File) : UnpackResult()
        data class ExistingFile(override val file: File) : UnpackResult()
    }

    fun unpack(packed: File): Result<UnpackResult, Exception> =
            Result.of {
                if (packed.isGzipped()) {
                    val target = packed.resolveSibling(File(packed.nameWithoutExtension))
                    if (target.exists()) {
                        if (target.isFile) {
                            logger.debug("Skipping unpack because '$target' already exists.")
                            UnpackResult.ExistingFile(target)
                        } else {
                            throw IllegalStateException("Can't unpack to '$target' - it exists and is not a file.")
                        }
                    } else {
                        UnpackResult.NewFile(packed.unGzip(target))
                    }
                } else {
                    throw IllegalArgumentException("Unable to unpack '$packed'.")
                }
            }
}

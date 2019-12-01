package org.mohme.gradle

import com.github.kittinunf.result.Result
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.Serializable
import java.net.URL
import java.nio.file.Path
import java.util.zip.GZIPInputStream


// TODO the whole thing is a totally broken abstraction :(
sealed class Executable : Serializable {
    protected val githubDownloadBasePath = "https://github.com/elm/compiler/releases/download"
    abstract fun path(logger: Logger, baseDir: File): Path

    @Suppress("ClassNaming")
    data class V_0_19_0(
            private val version: String = "0.19.0",
            private val artifactName: String = "binary-for-mac-64-bit.gz"
    ) : Executable() {
        override fun path(logger: Logger, baseDir: File): Path {
            // TODO implement platform distinction
            // TODO implement caching
            val url = URL("${githubDownloadBasePath}/${version}/${artifactName}")
            val targetFile = baseDir
                    .resolve("gradle-elm")
                    .resolve(version)
                    .resolve(artifactName)

            // TODO avoid exceptions - implement proper result handling
            Downloader(logger)
                    .fetch(url, targetFile = targetFile)

            val result = unGzip(targetFile)

            return result.fold<Path>({ file ->
                file.apply {
                    setExecutable(true)
                }.toPath()
            }, { exception ->
                // TODO handle failure
                throw exception
            })
        }
    }

    data class Provided(val name: String = "elm") : Executable() {
        override fun path(logger: Logger, baseDir: File) = Path.of(name)
    }
}

fun unGzip(gzFile: File): Result<File, Exception> {
    val buffer = ByteArray(1024 * 128)

    // TODO handle different compression formats!?
    // TODO make naming variable!?
    val targetFile = gzFile.resolveSibling("elm")

    return try {
        GZIPInputStream(FileInputStream(gzFile)).use { gzis ->
            FileOutputStream(targetFile).use { fos ->

                var len: Int
                while (gzis.read(buffer).also { len = it } > 0) {
                    fos.write(buffer, 0, len)
                }

            }
        }
        Result.Success(targetFile)
    } catch (e: IOException) {
        Result.Failure(e)
    }
}

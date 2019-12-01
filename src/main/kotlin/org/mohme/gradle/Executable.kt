package org.mohme.gradle

import com.github.kittinunf.result.Result
import com.github.kittinunf.result.map
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.Serializable
import java.net.URL
import java.nio.file.Path
import java.util.zip.GZIPInputStream


// TODO the whole thing is a totally broken abstraction :(
sealed class Executable : Serializable {
    protected val githubDownloadBasePath = "https://github.com/elm/compiler/releases/download"
    abstract fun path(logger: Logger, baseDir: File): Result<Path, Exception>

    @Suppress("ClassNaming")
    data class V_0_19_0(
            private val version: String = "0.19.0",
            private val artifactName: String = "binary-for-mac-64-bit.gz"
    ) : Executable() {
        override fun path(logger: Logger, baseDir: File): Result<Path, Exception> {
            // TODO implement platform distinction
            // TODO implement caching
            val url = URL("${githubDownloadBasePath}/${version}/${artifactName}")
            val targetFile = baseDir
                    .resolve("gradle-elm")
                    .resolve(version)
                    .resolve(artifactName)

            return Downloader(logger)
                    .fetch(url, targetFile = targetFile)
                    .map { downloaded -> unGzip(downloaded) }
                    .map { unpacked -> unpacked.apply { setExecutable(true) } }
                    .map { it.toPath() }
        }
    }

    data class Provided(val name: String = "elm") : Executable() {
        override fun path(logger: Logger, baseDir: File) = Result.of<Path, Exception> { Path.of(name) }
    }
}

fun unGzip(gzFile: File): File {
    val buffer = ByteArray(1024 * 128)

    // TODO handle different compression formats!?
    // TODO make naming variable!?
    val targetFile = gzFile.resolveSibling("elm")

    GZIPInputStream(FileInputStream(gzFile)).use { gzis ->
        FileOutputStream(targetFile).use { fos ->

            var len: Int
            while (gzis.read(buffer).also { len = it } > 0) {
                fos.write(buffer, 0, len)
            }

        }
    }

    return targetFile
}

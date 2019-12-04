package org.mohme.gradle

import com.github.kittinunf.result.Result
import com.github.kittinunf.result.flatMap
import com.github.kittinunf.result.map
import java.io.File
import java.io.Serializable
import java.net.URL
import java.nio.file.Path


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
            // TODO extract shareable code
            // TODO implement platform distinction
            val url = URL("${githubDownloadBasePath}/${version}/${artifactName}")
            val targetFile = baseDir
                    .resolve("gradle-elm")
                    .resolve(version)
                    .resolve(artifactName)

            val fileResult =
                    if (targetFile.exists()) Result.of { targetFile }
                    else Downloader(logger).fetch(url, targetFile)

            return fileResult
                    .flatMap { downloaded -> Unpacker.unpack(downloaded) }
                    .map { unpacked -> unpacked.apply { setExecutable(true) } }
                    .map { file -> file.toPath() }
        }
    }

    data class Provided(val name: String = "elm") : Executable() {
        override fun path(logger: Logger, baseDir: File) = Result.of<Path, Exception> { Path.of(name) }
    }
}

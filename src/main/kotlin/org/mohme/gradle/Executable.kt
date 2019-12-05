package org.mohme.gradle

import com.github.kittinunf.result.Result
import com.github.kittinunf.result.Result.Companion.success
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
            private val version: String = "0.19.0"
    ) : Executable() {
        override fun path(logger: Logger, baseDir: File): Result<Path, Exception> =
                // TODO extract shareable code
                platformCode
                        .map { artifactNameTemplate.replace("\$platformCode", it) }
                        .map { artifactName ->
                            Pair(artifactName, URL("${githubDownloadBasePath}/${version}/$artifactName"))
                        }
                        .map { (artifactName, url) ->
                            val targetFile = baseDir
                                    .resolve("gradle-elm")
                                    .resolve(version)
                                    .resolve(artifactName)
                            Pair(targetFile, url)
                        }
                        .flatMap { (targetFile, url) ->
                            if (targetFile.exists()) success(targetFile)
                            else Downloader(logger).fetch(url, targetFile)
                        }
                        .flatMap { downloaded -> Unpacker.unpack(downloaded) }
                        .map { unpacked -> unpacked.apply { setExecutable(true) } }
                        .map { file -> file.toPath() }
    }

    data class Provided(val name: String = "elm") : Executable() {
        override fun path(logger: Logger, baseDir: File) = Result.of<Path, Exception> { File(name).toPath() }
    }

    companion object {
        private const val artifactNameTemplate = "binary-for-\$platformCode-64-bit.gz"
        private val os = System.getProperty("os.name").toLowerCase()
        val platformCode = when {
            os.contains("nux") -> success("linux")
            os.contains("mac") -> success("mac")
            os.contains("win") -> success("windows")
            else ->
                error(IllegalStateException("Unable to determine platform (determining the correct file to download)"))
        }
    }
}

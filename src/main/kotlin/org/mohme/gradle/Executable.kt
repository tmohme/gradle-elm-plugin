package org.mohme.gradle

import com.github.kittinunf.result.Result
import com.github.kittinunf.result.Result.Companion.error
import com.github.kittinunf.result.Result.Companion.success
import com.github.kittinunf.result.flatMap
import com.github.kittinunf.result.map
import java.io.File
import java.io.Serializable
import java.net.URL
import java.nio.file.Path


sealed class Executable : Serializable {
    abstract fun path(logger: Logger, baseDir: File): Result<Path, Exception>

    object Provided : Executable() {
        override fun path(logger: Logger, baseDir: File) = Result.of<Path, Exception> { File("elm").toPath() }
    }

    sealed class Download(private val version: String) : Executable() {
        private val githubDownloadBasePath = "https://github.com/elm/compiler/releases/download"

        override fun path(logger: Logger, baseDir: File): Result<Path, Exception> =
                platformCode
                        .map { artifactName(it) }
                        .map { artifactName -> Pair(artifactName, artifactUrl(artifactName)) }
                        .map { (artifactName, artifactUrl) -> Pair(targetFile(baseDir, artifactName), artifactUrl) }
                        .flatMap { (targetFile, url) -> download(targetFile, logger, url) }
                        .flatMap { downloaded -> unpack(downloaded, logger) }
                        .map { unpacked -> unpacked.file.apply { setExecutable(true) } }
                        .map { file -> file.toPath() }

        private fun artifactName(platformCode: String) =
                artifactNameTemplate.replace("\$platformCode", platformCode)

        private fun artifactUrl(artifactName: String) =
                URL("${githubDownloadBasePath}/${version}/$artifactName")

        private fun targetFile(baseDir: File, artifactName: String): File {
            return baseDir
                    .resolve("gradle-elm")
                    .resolve(version)
                    .resolve(artifactName)
        }

        private fun download(targetFile: File, logger: Logger, url: URL) =
                if (targetFile.exists()) {
                    if (targetFile.isFile) {
                        logger.debug("Skipping download because '$targetFile' already exists.")
                        success(targetFile)
                    } else {
                        error(IllegalStateException("Can't download to '$targetFile' - it exists and is not a file."))
                    }
                } else {
                    Downloader(logger).fetch(url, targetFile)
                }

        private fun unpack(downloaded: File, logger: Logger) = Unpacker(logger).unpack(downloaded)

        @Suppress("ClassNaming")
        object V_0_19_0 : Download("0.19.0")

        @Suppress("ClassNaming")
        object V_0_19_1 : Download("0.19.1")

        companion object {
            private const val artifactNameTemplate = "binary-for-\$platformCode-64-bit.gz"
            private val os = System.getProperty("os.name").toLowerCase()
            val platformCode = when {
                os.contains("nux") -> success("linux")
                os.contains("mac") -> success("mac")
                os.contains("win") -> success("windows")
                else ->
                    @Suppress("MaxLineLength")
                    error(IllegalStateException("Unable to determine platform (determining the correct file to download)"))
            }
        }
    }
}

package org.mohme.gradle

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.map
import java.io.File
import java.io.IOException
import java.net.URL

private const val PERCENT_FACTOR = 100

class Downloader(private val logger: Logger) {

    // TODO do not require targetFile as input
    fun fetch(url: URL, targetFile: File) =
            Result.of<File, Exception> { createParentDir(targetFile) }
                    .map { _ -> download(url, targetFile) }

    private fun createParentDir(file: File): File {
        val targetDirectory = file.parentFile
        if (targetDirectory.exists()) {
            if (!targetDirectory.isDirectory) {
                @Suppress("MaxLineLength")
                throw IOException("Can't create target directory '$targetDirectory' because there already exists a file with this name")
            }
        } else {
            if (!targetDirectory.mkdirs()) {
                throw IOException("Unable to create target directory '$targetDirectory'")
            }
        }
        return targetDirectory
    }

    private fun download(url: URL, targetFile: File): File {
        val cancellableRequest = Fuel.download(url.toString())
                .apply {
                    timeout(CONNECT_TIMEOUT_IN_MS)
                    timeoutRead(READ_TIMEOUT_IN_MS)
                }
                .fileDestination { _, _ -> targetFile }
                .progress { readBytes, totalBytes ->
                    val progress = readBytes.toFloat() / totalBytes.toFloat() * PERCENT_FACTOR
                    logger.debug("Bytes downloaded {} / {} ({} %)", targetFile, totalBytes, progress)
                }
                .response { result ->
                    when (result) {
                        is Result.Success -> logger.debug("Download of '{}' succeeded", url)
                        is Result.Failure -> logger.error("Download of '{}' failed ({})", url, result.error)
                    }
                }

        val response = cancellableRequest.join()
        // TODO better handling of unsuccessful response (e.g. due to timeout) - at least log error
        logger.debug("response=$response")
        if (!targetFile.exists() || !targetFile.isFile) {
            throw IOException("Unable to fetch '$url' into '$targetFile'.")
        }

        return targetFile
    }

    companion object {
        const val CONNECT_TIMEOUT_IN_MS = 10_000
        const val READ_TIMEOUT_IN_MS = 30_000
    }
}

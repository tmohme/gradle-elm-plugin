package org.mohme.gradle

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result
import org.gradle.api.logging.Logger
import java.io.File
import java.io.IOException
import java.net.URL

private const val PERCENT_FACTOR = 100
class Downloader(val logger : Logger) {

    // TODO return Success(File)
    @Suppress("ThrowsCount")
    fun fetch(url: URL, targetFile: File) {
        val targetDirectory = targetFile.parentFile
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

        val cancellableRequest = Fuel.download(url.toString())
                .fileDestination { _, _ -> logger.debug("fileDestination='$targetFile'"); targetFile }
                .progress { readBytes, totalBytes ->
                    val progress = readBytes.toFloat() / totalBytes.toFloat() * PERCENT_FACTOR
                    logger.debug("Bytes downloaded $readBytes / $totalBytes ($progress %)")
                }
                .response { result ->
                    when (result) {
                        is Result.Success -> logger.debug("Download of '$url' succeeded")
                        is Result.Failure -> logger.error("Download of '$url' failed (${result.error})")
                    }
                }

        val response = cancellableRequest.join()
        logger.debug("response=$response")
        if (!targetFile.exists() || !targetFile.isFile) {
            throw IOException("Unable to fetch '$url' into '$targetFile'.")
        }
    }
}

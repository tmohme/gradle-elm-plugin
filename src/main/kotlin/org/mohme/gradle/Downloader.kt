package org.mohme.gradle

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result
import org.gradle.api.logging.Logger
import java.io.File
import java.net.URL

class Downloader(val logger : Logger) {

    fun fetch(url: URL, targetFile: File) {
        val cancellableRequest = Fuel
                .download(url.toString())
                .fileDestination { _, _ -> println("fileDestination='$targetFile'"); targetFile }
                .progress { readBytes, totalBytes ->
                    val progress = readBytes.toFloat() / totalBytes.toFloat() * 100
                    logger.debug("Bytes downloaded $readBytes / $totalBytes ($progress %)")
                }
                .response { result ->
                    when (result) {
                        is Result.Success -> logger.debug("Download of '$url' succeeded")
                        is Result.Failure -> logger.quiet("Download of '$url' failed (${result.error})")
                    }
                }

        cancellableRequest.join()
    }
}

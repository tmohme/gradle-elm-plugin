package org.mohme.gradle

import org.gradle.api.logging.Logger
import java.io.Serializable
import java.net.URL
import java.nio.file.Path

// TODO should not depend on gradle
// TODO the whole thing is a totally broken abstraction :(
sealed class Executable : Serializable {
    protected val githubDownloadBasePath = "https://github.com/elm/compiler/releases/download"
    abstract fun path(logger: Logger): Path

    @Suppress("ClassNaming")
    data class V_0_19_1(
            private val version: String = "0.19.1",
            private val artifactName : String = "binary-for-mac-64-bit.gz"
    ) : Executable() {
        override fun path(logger: Logger): Path {
            // TODO implement platform distinction
            // TODO implement caching
            val url = URL("${githubDownloadBasePath}/${version}/${artifactName}")
            Downloader(logger)
                    .fetch(url, targetFile = Path.of("build", "gradle-elm", version, artifactName).toFile())

            // TODO implement unpack

            TODO("not implemented")
        }
    }

    data class Provided(val name: String = "elm") : Executable() {
        override fun path(logger: Logger) = Path.of(name)
    }
}

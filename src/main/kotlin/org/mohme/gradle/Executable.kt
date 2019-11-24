package org.mohme.gradle

import org.gradle.api.logging.Logger
import java.io.Serializable
import java.net.URL
import java.nio.file.Path

sealed class Executable : Serializable {
    abstract fun path(logger: Logger): Path

    @Suppress("ClassNaming")
    data class V_0_19_1(
            val url: URL =
                    URL("https://github.com/elm/compiler/releases/download/0.19.1/binary-for-mac-64-bit.gz")
    ) : Executable() {
        override fun path(logger: Logger): Path {
            // TODO implement platform distinction
            // TODO implement caching
            Downloader(logger).fetch(url, targetFile = Path.of("build", "gradle-elm", "0.19.0").toFile())

            // TODO implement unpack

            TODO("not implemented")
        }
    }

    data class Provided(val name: String = "elm") : Executable() {
        override fun path(logger: Logger) = Path.of(name)
    }
}

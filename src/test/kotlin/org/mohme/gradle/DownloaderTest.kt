package org.mohme.gradle

import org.gradle.internal.impldep.org.junit.Test
import org.junit.jupiter.api.io.TempDir
import strikt.api.expectThat
import strikt.assertions.isTrue
import java.io.File

internal class DownloaderTest {

    @Test
    fun fetch(@TempDir tempDir: File) {
        // given
        val logger = aLogger()

        // when
        val path = Executable.V_0_19_0().path(logger, tempDir).get()

        // then
        expectThat(path.toFile()) {
            get(File::exists).isTrue()
            get(File::isFile).isTrue()
            get(File::canExecute).isTrue()
        }
    }

    private fun aLogger() = object : Logger {
        override fun debug(message: String, vararg objects: Any?) {}
        override fun error(message: String, vararg objects: Any?) {}
    }
}

package org.mohme.gradle

import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logger
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.slf4j.Marker
import strikt.api.expectThat
import strikt.assertions.isTrue
import java.io.File

internal class DownloaderTest {

    // TODO replace when mockk works correct
    val logger = object : Logger {
        override fun warn(p0: String?) {}
        override fun warn(p0: String?, p1: Any?) {}
        override fun warn(p0: String?, vararg p1: Any?) {}
        override fun warn(p0: String?, p1: Any?, p2: Any?) {}
        override fun warn(p0: String?, p1: Throwable?) {}
        override fun warn(p0: Marker?, p1: String?) {}
        override fun warn(p0: Marker?, p1: String?, p2: Any?) {}
        override fun warn(p0: Marker?, p1: String?, p2: Any?, p3: Any?) {}
        override fun warn(p0: Marker?, p1: String?, vararg p2: Any?) {}
        override fun warn(p0: Marker?, p1: String?, p2: Throwable?) {}

        override fun isQuietEnabled() = false

        override fun getName() = "MockLogger"

        override fun info(message: String?, vararg objects: Any?) {}
        override fun info(p0: String?) {}
        override fun info(p0: String?, p1: Any?) {}
        override fun info(p0: String?, p1: Any?, p2: Any?) {}
        override fun info(p0: String?, p1: Throwable?) {}
        override fun info(p0: Marker?, p1: String?) {}
        override fun info(p0: Marker?, p1: String?, p2: Any?) {}
        override fun info(p0: Marker?, p1: String?, p2: Any?, p3: Any?) {}
        override fun info(p0: Marker?, p1: String?, vararg p2: Any?) {}
        override fun info(p0: Marker?, p1: String?, p2: Throwable?) {}

        override fun isErrorEnabled() = false
        override fun isErrorEnabled(p0: Marker?) = false

        override fun error(p0: String?) {}
        override fun error(p0: String?, p1: Any?) {}
        override fun error(p0: String?, p1: Any?, p2: Any?) {}
        override fun error(p0: String?, vararg p1: Any?) {}
        override fun error(p0: String?, p1: Throwable?) {}
        override fun error(p0: Marker?, p1: String?) {}
        override fun error(p0: Marker?, p1: String?, p2: Any?) {}
        override fun error(p0: Marker?, p1: String?, p2: Any?, p3: Any?) {}
        override fun error(p0: Marker?, p1: String?, vararg p2: Any?) {}
        override fun error(p0: Marker?, p1: String?, p2: Throwable?) {}

        override fun isDebugEnabled() = false
        override fun isDebugEnabled(p0: Marker?) = false

        override fun log(level: LogLevel?, message: String?) {}
        override fun log(level: LogLevel?, message: String?, vararg objects: Any?) {}
        override fun log(level: LogLevel?, message: String?, throwable: Throwable?) {}

        override fun debug(message: String?, vararg objects: Any?) {}
        override fun debug(p0: String?) {}
        override fun debug(p0: String?, p1: Any?) {}
        override fun debug(p0: String?, p1: Any?, p2: Any?) {}
        override fun debug(p0: String?, p1: Throwable?) {}
        override fun debug(p0: Marker?, p1: String?) {}
        override fun debug(p0: Marker?, p1: String?, p2: Any?) {}
        override fun debug(p0: Marker?, p1: String?, p2: Any?, p3: Any?) {}
        override fun debug(p0: Marker?, p1: String?, vararg p2: Any?) {}
        override fun debug(p0: Marker?, p1: String?, p2: Throwable?) {}

        override fun isEnabled(level: LogLevel?) = false

        override fun lifecycle(message: String?) {}
        override fun lifecycle(message: String?, vararg objects: Any?) {}
        override fun lifecycle(message: String?, throwable: Throwable?) {}

        override fun quiet(message: String?) {}
        override fun quiet(message: String?, vararg objects: Any?) {}
        override fun quiet(message: String?, throwable: Throwable?) {}

        override fun isLifecycleEnabled() = false

        override fun isInfoEnabled() = false
        override fun isInfoEnabled(p0: Marker?) = false

        override fun trace(p0: String?) {}
        override fun trace(p0: String?, p1: Any?) {}
        override fun trace(p0: String?, p1: Any?, p2: Any?) {}
        override fun trace(p0: String?, vararg p1: Any?) {}
        override fun trace(p0: String?, p1: Throwable?) {}
        override fun trace(p0: Marker?, p1: String?) {}
        override fun trace(p0: Marker?, p1: String?, p2: Any?) {}
        override fun trace(p0: Marker?, p1: String?, p2: Any?, p3: Any?) {}
        override fun trace(p0: Marker?, p1: String?, vararg p2: Any?) {}
        override fun trace(p0: Marker?, p1: String?, p2: Throwable?) {}

        override fun isWarnEnabled() = false
        override fun isWarnEnabled(p0: Marker?) = false

        override fun isTraceEnabled() = false
        override fun isTraceEnabled(p0: Marker?) = false
    }

    @Test
    fun fetch(@TempDir tempDir: File) {
        val targetFile = tempDir.resolve("target")

        val path = Executable.V_0_19_1().path(logger)

        expectThat(path.toFile()) {
            get(File::exists).isTrue()
            get(File::isFile).isTrue()
        }
    }
}

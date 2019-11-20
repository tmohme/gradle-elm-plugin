package org.mohme.gradle

import java.io.Serializable
import java.nio.file.Path

sealed class Executable : Serializable {
    abstract val path: Path

    data class Provided(val name: String = "elm") : Executable() {
        override val path: Path
            get() = Path.of(name)
    }
}

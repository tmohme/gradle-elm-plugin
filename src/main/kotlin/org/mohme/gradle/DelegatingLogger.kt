package org.mohme.gradle

class DelegatingLogger(private val gradleLogger : org.gradle.api.logging.Logger) : Logger{
    override fun debug(message: String, vararg objects: Any?) {
        gradleLogger.debug(message, *objects)
    }

    override fun error(message: String, vararg objects: Any?) {
        gradleLogger.error(message, *objects)
    }
}

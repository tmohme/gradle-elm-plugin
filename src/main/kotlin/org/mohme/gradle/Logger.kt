package org.mohme.gradle

interface Logger {
    fun debug(message: String, vararg objects: Any?)
    fun error(message: String, vararg objects: Any?)
}

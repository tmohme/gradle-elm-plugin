package org.mohme.gradle

import org.gradle.api.Project
import org.gradle.kotlin.dsl.property

const val DEFAULT_BUILD_DIR = "elm"
const val DEFAULT_MAIN_MODULE = "Main.elm"
const val DEFAULT_SOURCE_DIR = "src/elm"
const val DEFAULT_TARGET_MODULE = "elm.js"
const val DEFAULT_TEST_EXECUTABLE = "elm-test"

const val CWD = "."

open class ElmPluginExtension(project: Project) {
    private val objectFactory = project.objects

    val executable = objectFactory.property<Executable>().convention(Executable.Provided)
    val executionDir = objectFactory.property<String>().convention(CWD)

    val sourceDir =
            objectFactory.directoryProperty().convention(project.layout.projectDirectory.dir(DEFAULT_SOURCE_DIR))
    val mainModuleName = objectFactory.property<String>().convention(DEFAULT_MAIN_MODULE)

    val buildDir = objectFactory.directoryProperty().convention(project.layout.buildDirectory.dir(DEFAULT_BUILD_DIR))
    val targetModuleName = objectFactory.property<String>().convention(DEFAULT_TARGET_MODULE)

    val debug = objectFactory.property<Boolean>().convention(false)
    val optimize = objectFactory.property<Boolean>().convention(false)

    val testExecutable = objectFactory.property<String>().convention(DEFAULT_TEST_EXECUTABLE)
    val testExecutionDir = objectFactory.property<String>().convention(CWD)
}

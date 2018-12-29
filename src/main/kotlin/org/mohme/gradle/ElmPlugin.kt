package org.mohme.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import java.nio.file.Paths


class ElmPlugin : Plugin<Project> {
    val DEFAULT_EXECUTABLE = "elm"
    val DEFAULT_MAIN_MODULE = "Main.elm"
    val DEFAULT_TARGET_MODULE = "elm.js"
    val DEFAULT_TEST_EXECUTABLE = "elm-test"


    override fun apply(project: Project) {
        val elm = project.extensions.create("elm", ElmPluginExtension::class, project)

        elm.executable.set(DEFAULT_EXECUTABLE)
        elm.executionDir.set(".")

        elm.sourceDir.set(Paths.get("src", "elm").toFile())
        elm.mainModuleName.set(DEFAULT_MAIN_MODULE)

        elm.buildDir.set(Paths.get(project.buildDir.path, "elm").toFile())
        elm.targetModuleName.set(DEFAULT_TARGET_MODULE)

        elm.debug.set(false)
        elm.optimize.set(false)

        elm.testExecutable.set(DEFAULT_TEST_EXECUTABLE)
        elm.testExecutionDir.set(".")

        project.tasks.create("elmMake", ElmMakeTask::class) {
            // configure defaults
            executable.set(elm.executable)
            executionDir.set(elm.executionDir)

            sourceDir.set(elm.sourceDir)
            mainModuleName.set(elm.mainModuleName)

            buildDir.set(elm.buildDir)
            targetModuleName.set(elm.targetModuleName)

            debug.set(elm.debug)
            optimize.set(elm.optimize)
        }

        project.tasks.create("elmTest", ElmTestTask::class) {
            // configure defaults
            executable.set(elm.testExecutable)
            executionDir.set(elm.testExecutionDir)
        }
    }

}

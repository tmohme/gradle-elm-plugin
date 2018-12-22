package org.mohme.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.nio.file.Paths


class ElmPlugin : Plugin<Project> {
    val DEFAULT_EXECUTABLE = "elm"
    val DEFAULT_MAIN_MODULE = "Main.elm"
    val DEFAULT_TARGET_MODULE = "elm.js"


    override fun apply(project: Project) {
        val elm = project.extensions.create<ElmPluginExtension>("elm", ElmPluginExtension::class.java, project)

        elm.executable.set(DEFAULT_EXECUTABLE)
        elm.executionDir.set(".")

        elm.sourceDir.set(Paths.get("src", "elm").toFile())
        elm.mainModuleName.set(DEFAULT_MAIN_MODULE)

        elm.buildDir.set(Paths.get(project.buildDir.path, "elm").toFile())
        elm.targetModuleName.set(DEFAULT_TARGET_MODULE)

        elm.debug.set(false)
        elm.optimize.set(false)


        project.tasks.create("elmMake", ElmMakeTask::class.java) { task ->
            // configure defaults
            task.executable = elm.executable
            task.setExecutionDir(elm.executionDir)

            task.setSourceDir(elm.sourceDir)
            task.mainModuleName = elm.mainModuleName

            task.buildDir = elm.buildDir
            task.targetModuleName = elm.targetModuleName

            task.debug = elm.debug
            task.setOptimize(elm.optimize)
        }
    }

}

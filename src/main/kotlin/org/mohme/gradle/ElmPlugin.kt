package org.mohme.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create


class ElmPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val elm = project.extensions.create("elm", ElmPluginExtension::class, project)

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

package org.mohme.gradle

import org.gradle.api.Project
import org.gradle.kotlin.dsl.property

open class ElmPluginExtension(project: Project) {
    private val objectFactory = project.objects

    val executable = objectFactory.property<Executable>() // TODO .convention()
    val executionDir = objectFactory.property<String>()

    val sourceDir = objectFactory.directoryProperty()
    val mainModuleName = objectFactory.property<String>()

    val buildDir = objectFactory.directoryProperty()
    val targetModuleName = objectFactory.property<String>()

    val debug = objectFactory.property<Boolean>()
    val optimize = objectFactory.property<Boolean>()

    val testExecutable = objectFactory.property<String>()
    val testExecutionDir = objectFactory.property<String>()
}

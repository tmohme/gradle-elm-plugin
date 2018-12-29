package org.mohme.gradle

import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.property

open class ElmPluginExtension(project: Project) {
    var executable: Property<String>
    var executionDir: Property<String>

    var sourceDir: DirectoryProperty
    var mainModuleName: Property<String>

    var buildDir: DirectoryProperty
    var targetModuleName: Property<String>

    var debug: Property<Boolean>
    var optimize: Property<Boolean>

    var testExecutable: Property<String>
    var testExecutionDir: Property<String>



    init {
        val objectFactory = project.objects

        executable = objectFactory.property(String::class)
        executionDir = objectFactory.property(String::class)

        sourceDir = objectFactory.directoryProperty()
        mainModuleName = objectFactory.property(String::class)

        buildDir = objectFactory.directoryProperty()
        targetModuleName = objectFactory.property(String::class)

        debug = objectFactory.property(Boolean::class)
        optimize = objectFactory.property(Boolean::class)

        testExecutable = objectFactory.property(String::class)
        testExecutionDir = objectFactory.property(String::class)
    }
}

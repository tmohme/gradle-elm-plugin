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

        executable = objectFactory.property()
        executionDir = objectFactory.property()

        // use deprecated "project.layout.directoryProperty()" instead of "objectFactory.directoryProperty()" to be compatible with Gradle 4
        sourceDir = project.layout.directoryProperty()
        mainModuleName = objectFactory.property()

        // use deprecated "project.layout.directoryProperty()" instead of "objectFactory.directoryProperty()" to be compatible with Gradle 4
        buildDir = project.layout.directoryProperty()
        targetModuleName = objectFactory.property()

        debug = objectFactory.property()
        optimize = objectFactory.property()

        testExecutable = objectFactory.property()
        testExecutionDir = objectFactory.property()
    }
}

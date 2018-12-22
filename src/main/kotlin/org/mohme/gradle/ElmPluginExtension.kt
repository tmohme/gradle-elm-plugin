package org.mohme.gradle

import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.property
import java.io.File

open class ElmPluginExtension(project: Project) {
    var executable: Property<String>
    var executionDir: Property<String>

    var sourceDir: DirectoryProperty
    var mainModuleName: Property<String>

    var buildDir: DirectoryProperty
    var targetModuleName: Property<String>

    var debug: Property<Boolean>
    var optimize: Property<Boolean>


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
    }

    fun setExecutable(executable: String) {
        this.executable.set(executable)
    }

    fun setExecutionDir(executionDir: String) {
        this.executionDir.set(executionDir)
    }

    fun setSourceDir(sourceDir: File) {
        this.sourceDir.set(sourceDir)
    }

    fun setMainModuleName(mainModuleName: String) {
        this.mainModuleName.set(mainModuleName)
    }

    fun setBuildDir(buildDir: File) {
        this.buildDir.set(buildDir)
    }

    fun setTargetModuleName(targetModuleName: String) {
        this.targetModuleName.set(targetModuleName)
    }

    fun setDebug(debug: Boolean?) {
        this.debug.set(debug)
    }

    fun setOptimize(optimize: Boolean?) {
        this.optimize.set(optimize)
    }

}

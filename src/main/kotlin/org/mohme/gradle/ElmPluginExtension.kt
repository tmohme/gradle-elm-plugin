package org.mohme.gradle

import org.gradle.api.Project
import org.gradle.api.provider.Property
import java.io.File

open class ElmPluginExtension(project: Project) {
    var executable: Property<String>
    var executionDir: Property<String>

    var sourceDir: Property<File>
    var mainModuleName: Property<String>

    var targetModuleName: Property<String>
    var buildDir: Property<File>

    var debug: Property<Boolean>
    var optimize: Property<Boolean>


    init {
        val objectFactory = project.objects

        executable = objectFactory.property(String::class.java)
        executionDir = objectFactory.property(String::class.java)

        sourceDir = objectFactory.property(File::class.java)
        mainModuleName = objectFactory.property(String::class.java)

        buildDir = objectFactory.property(File::class.java)
        targetModuleName = objectFactory.property(String::class.java)

        debug = objectFactory.property(Boolean::class.java)
        optimize = objectFactory.property(Boolean::class.java)
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

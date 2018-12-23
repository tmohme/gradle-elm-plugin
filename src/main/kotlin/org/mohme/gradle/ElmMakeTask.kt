package org.mohme.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.property
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.nio.file.Paths
import java.util.*


@CacheableTask
open class ElmMakeTask : DefaultTask() {

    @Input
    var executable: Property<String>

    @Input
    var executionDir: Property<String>

    @PathSensitive(PathSensitivity.RELATIVE)
    @InputDirectory
    var sourceDir: DirectoryProperty

    @Input
    var mainModuleName: Property<String>

    @OutputDirectory
    var buildDir: DirectoryProperty

    @Input
    var targetModuleName: Property<String>

    @Input
    var debug: Property<Boolean>

    @Input
    var optimize: Property<Boolean>

    init {
        group = "Build"
        description = "Run `elm make`."

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


    @TaskAction
    fun make() {
        val elmMakeCmd = ArrayList<String>()
        val osName = System.getProperty("os.name")
        if (osName.startsWith("Windows")) {
            elmMakeCmd.add("cmd")
            elmMakeCmd.add("/c")
        }
        elmMakeCmd.add(executable.get())
        elmMakeCmd.add("make")
        elmMakeCmd.add(Paths.get(sourceDir.get().toString(), mainModuleName.get()).toString())
        elmMakeCmd.add("--output")
        elmMakeCmd.add(Paths.get(buildDir.get().toString(), targetModuleName.get()).toString())
        if (debug.get() && optimize.get()) {
            throw TaskExecutionException(
                    this,
                    IllegalArgumentException(
                            "I cannot compile with 'optimize' and 'debug' at the same time.")
            )
        }
        if (debug.get()) {
            elmMakeCmd.add("--debug")
        }
        if (optimize.get()) {
            elmMakeCmd.add("--optimize")
        }

        elmMake(elmMakeCmd)
    }

    private fun elmMake(makeCmd: List<String>) {
        val cmdString = makeCmd.joinToString(" ")
        logger.info("executing '{}'", cmdString)

        val process: Process
        try {
            process = ProcessBuilder(makeCmd)
                    .directory(File(executionDir.get()))
                    .start()
        } catch (e: IOException) {
            throw GradleException("failed to execute '${cmdString}'", e)
        }

        process.waitFor()

        val stdOut = BufferedReader(InputStreamReader(process.inputStream))
        stdOut.lineSequence().forEach { line -> logger.info(line) }

        val stdErr = BufferedReader(InputStreamReader(process.errorStream))
        stdErr.lineSequence().forEach { line -> logger.error(line) }

        if (process.exitValue() != 0) {
            throw GradleException("'${cmdString}' failed.")
        }
    }

}

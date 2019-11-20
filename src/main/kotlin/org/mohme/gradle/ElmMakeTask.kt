package org.mohme.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException
import org.gradle.kotlin.dsl.property
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.nio.file.Paths


@CacheableTask
open class ElmMakeTask : DefaultTask() {
    private val objectFactory get() = project.objects

    @Input
    val executable = objectFactory.property<Executable>()

    @Input
    val executionDir = objectFactory.property<String>()

    @PathSensitive(PathSensitivity.RELATIVE)
    @InputDirectory
    val sourceDir = objectFactory.directoryProperty()

    @Input
    val mainModuleName = objectFactory.property<String>()

    @OutputDirectory
    val buildDir = objectFactory.directoryProperty()

    @Input
    val targetModuleName = objectFactory.property<String>()

    @Input
    val debug = objectFactory.property<Boolean>()

    @Input
    val optimize = objectFactory.property<Boolean>()

    init {
        group = "Build"
        description = "Run `elm make`."
    }


    @TaskAction
    fun make() {
        val elmMakeCmd = ArrayList<String>()
        val osName = System.getProperty("os.name")
        if (osName.startsWith("Windows")) {
            elmMakeCmd.add("cmd")
            elmMakeCmd.add("/c")
        }
        elmMakeCmd.add(executable.get().path.toString())
        elmMakeCmd.add("make")
        elmMakeCmd.add(Paths.get(sourceDir.get().toString(), mainModuleName.get()).toString())
        elmMakeCmd.add("--output")
        elmMakeCmd.add(Paths.get(buildDir.get().toString(), targetModuleName.get()).toString())
        if (debug.get() && optimize.get()) {
            throw TaskExecutionException(
                    this,
                    IllegalArgumentException("I cannot compile with 'optimize' and 'debug' at the same time.")
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

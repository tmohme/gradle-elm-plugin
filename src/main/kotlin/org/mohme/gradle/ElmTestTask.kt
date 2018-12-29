package org.mohme.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.property
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.util.*


@CacheableTask
open class ElmTestTask : DefaultTask() {

    @Input
    var executable: Property<String>

    @Input
    var executionDir: Property<String>

    init {
        group = "Verification"
        description = "Run `elm-test`."

        val objectFactory = project.objects

        executable = objectFactory.property(String::class)
        executionDir = objectFactory.property(String::class)
    }


    @TaskAction
    fun test() {
        val elmTestCmd = ArrayList<String>()
        val osName = System.getProperty("os.name")
        if (osName.startsWith("Windows")) {
            elmTestCmd.add("cmd")
            elmTestCmd.add("/c")
        }
        elmTestCmd.add(executable.get())

        elmTest(elmTestCmd)
    }

    private fun elmTest(testCmd: List<String>) {
        val cmdString = testCmd.joinToString(" ")
        logger.info("executing '{}'", cmdString)

        val process: Process
        try {
            process = ProcessBuilder(testCmd)
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

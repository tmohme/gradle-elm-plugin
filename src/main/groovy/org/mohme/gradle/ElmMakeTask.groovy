package org.mohme.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.nio.file.Paths

class ElmMakeTask extends DefaultTask {
    private Logger logger

    @Input String executable = "elm-make"
    @Input File executionDir = new File(".")
    File sourceDir = Paths.get("src", "elm").toFile()
    @Input String mainModule = 'Main.elm'
    File buildDir = new File("${project.buildDir.path}", "elm")
    @Input String targetModule = 'elm.js'
    @Input boolean confirm = true
    @Input boolean debug = false
    @Input boolean warn = false

    @InputDirectory
    private getSourceDir() {
        project.file(sourceDir)
    }

    @OutputDirectory
    private getBuildDir() {
        project.file(buildDir)
    }

    ElmMakeTask() {
        group 'Build'
        description 'Run `elm-make`.'
    }

    @TaskAction
    make() {
        logger = project.logger

        String[] elmMakeCmd
        if (System.getProperty("os.name").startsWith("Windows")) {
        	elmMakeCmd = ["cmd", "/c", executable, Paths.get(getSourceDir().path, mainModule).toString(),
                            "--output", Paths.get(getBuildDir().path, targetModule).toString()]
           logger.info("Adding windows specific command string")
        } else {
            elmMakeCmd = [executable, Paths.get(getSourceDir().path, mainModule).toString(),
                          "--output", Paths.get(getBuildDir().path, targetModule).toString()]
        }
        if (confirm) {
            elmMakeCmd += '--yes'
        }
        if (debug) {
            elmMakeCmd += '--debug'
        }
        if (warn) {
            elmMakeCmd += '--warn'
        }

        elmMake(elmMakeCmd)
    }

    def elmMake(String[] makeCmd) {
        logger.info(String.format("executing '%s'", Arrays.toString(makeCmd)))

        Process process
        try {
            process= new ProcessBuilder(makeCmd)
            .directory(executionDir)
            .start()
        } catch (IOException e) {
            String msg = String.format("failed to execute '%s'", Arrays.toString(makeCmd))
            throw new GradleException(msg, e)
        }

        // collect output
        BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()))
        String stdOutLine
        BufferedReader stdErr = new BufferedReader(new InputStreamReader(process.getErrorStream()))
        String stdErrLine
        List<String> stdOutLines = new ArrayList<String>()
        List<String> stdErrLines = new ArrayList<String>()
        try {
            while (((stdOutLine = stdOut.readLine()) != null) || ((stdErrLine = stdErr.readLine()) != null)) {
                if (stdOutLine != null) {
                    stdOutLines.add(stdOutLine)
                }
                if (stdErrLine != null) {
                    stdErrLines.add(stdErrLine)
                }

                stdErrLine = null
            }
        } catch (IOException e) {
            String msg = String.format("failed to collect output from '%s'", Arrays.toString(makeCmd))
            throw new GradleException(msg, e)
        }

        stdOutLines.each {
            logger.info(it)
        }

        stdErrLines.each {
            logger.error(it)
        }

        final boolean successful = stdErrLines.isEmpty()
        if (!successful) {
            throw new GradleException("elm-make failed; see the compiler error output for details.")
        }
    }
}

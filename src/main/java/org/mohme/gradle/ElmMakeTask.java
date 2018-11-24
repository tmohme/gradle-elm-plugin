package org.mohme.gradle;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;


public class ElmMakeTask extends DefaultTask {
    private Logger logger = getLogger();

    @Input
    String executable;
    @Input
    String executionDir;
    @Input
    String mainModuleName;
    @Input
    String targetModuleName;
    @Input
    boolean debug;
    @Input
    boolean optimize;

    private File sourceDir;

    public ElmMakeTask() {
        setGroup("Build");
        setDescription("Run `elm make`.");
    }
    
    @InputDirectory
    File getSourceDir() {
        return sourceDir;
    }

    void setSourceDir(File sourceDir) {
        this.sourceDir = sourceDir;
    }
    
    private File buildDir;
    
    @OutputDirectory
    File getBuildDir() {
        return buildDir;
    }

    void setBuildDir(File buildDir) {
        this.buildDir = buildDir;
    }

    @TaskAction
    public void make() {
        final List<String> elmMakeCmd = new ArrayList<>();
        final String osName = System.getProperty("os.name");
        if (osName.startsWith("Windows")) {
            elmMakeCmd.add("cmd");
            elmMakeCmd.add("/c");
        }
        elmMakeCmd.add(executable);
        elmMakeCmd.add("make");
        elmMakeCmd.add(Paths.get(getSourceDir().getPath(), mainModuleName).toString());
        elmMakeCmd.add("--output");
        elmMakeCmd.add(Paths.get(getBuildDir().getPath(), targetModuleName).toString());
        if (debug && optimize) {
            throw new TaskExecutionException(
                    this,
                    new IllegalArgumentException(
                            "I cannot compile with 'optimize' and 'debug' at the same time.")
            );
        }
        if (debug) {
            elmMakeCmd.add("--debug");
        }
        if (optimize) {
            elmMakeCmd.add("--optimize");
        }

        elmMake(elmMakeCmd);
    }

    private void elmMake(List<String> makeCmd) {
        if (logger.isInfoEnabled()) {
            logger.info("executing '{}'", String.join(" ", makeCmd));
        }

        Process process;
        try {
            process = new ProcessBuilder(makeCmd)
                    .directory(new File(executionDir))
                    .start();
        } catch (IOException e) {
            String msg = String.format("failed to execute '%s'", String.join(" ", makeCmd));
            throw new GradleException(msg, e);
        }

        // collect output
        final BufferedReader stdOut =
                new BufferedReader(new InputStreamReader(process.getInputStream()));
        String stdOutLine;
        final BufferedReader stdErr =
                new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String stdErrLine = null;
        List<String> stdOutLines = new ArrayList<>();
        List<String> stdErrLines = new ArrayList<>();
        try {
            while (((stdOutLine = stdOut.readLine()) != null) ||
                   ((stdErrLine = stdErr.readLine()) != null)) {
                if (stdOutLine != null) {
                    stdOutLines.add(stdOutLine);
                }
                if (stdErrLine != null) {
                    stdErrLines.add(stdErrLine);
                }

                stdErrLine = null;
            }
        } catch (IOException e) {
            String msg = String.format(
                    "failed to collect output from '%s'",
                    String.join(" ", makeCmd)
            );
            throw new GradleException(msg, e);
        }

        stdOutLines.forEach(line -> logger.info(line));
        stdErrLines.forEach(line -> logger.info(line));

        final boolean successful = stdErrLines.isEmpty();
        if (!successful) {
            throw new GradleException(
                    "'elm make' failed; see the compiler error output for details.");
        }
    }

}

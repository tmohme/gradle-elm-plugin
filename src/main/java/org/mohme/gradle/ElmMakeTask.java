package org.mohme.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("UnstableApiUsage")
public class ElmMakeTask extends DefaultTask {
    private Logger logger = getLogger();

    @Input
    Property<String> executable;

    @Input
    Property<String> executionDir;

    @InputDirectory
    Property<File> sourceDir;

    @Input
    Property<String> mainModuleName;

    @OutputDirectory
    Property<File> buildDir;

    @Input
    Property<String> targetModuleName;

    @Input
    Property<Boolean> debug;
    @Input
    Property<Boolean> optimize;


    public ElmMakeTask() {
        setGroup("Build");
        setDescription("Run `elm make`.");

        final ObjectFactory objectFactory = getProject().getObjects();

        executable = objectFactory.property(String.class);
        executionDir = objectFactory.property(String.class);

        sourceDir = objectFactory.property(File.class);
        mainModuleName = objectFactory.property(String.class);

        buildDir = objectFactory.property(File.class);
        targetModuleName = objectFactory.property(String.class);

        debug = objectFactory.property(Boolean.class);
        optimize = objectFactory.property(Boolean.class);
    }

    public Property<String> getExecutable() {
        return executable;
    }
    public void setExecutable(Property<String> executable) {
        this.executable = executable;
    }
    public void setExecutable(String executable) {
        this.executable.set(executable);
    }

    public Property<String> getExecutionDir() {
        return executable;
    }
    public void setExecutionDir(Property<String> executionDir) {
        this.executionDir = executionDir;
    }
    public void setExecutionDir(String executionDir) {
        this.executionDir.set(executionDir);
    }

    public Property<File> getSourceDir() {
        return sourceDir;
    }
    public void setSourceDir(Property<File> sourceDir) {
        logger.lifecycle("task: setting task sourceDirP to " + sourceDir);
        this.sourceDir = sourceDir;
    }
    public void setSourceDir(File sourceDir) {
        logger.lifecycle("task: setting task sourceDir to " + sourceDir);
        this.sourceDir.set(sourceDir);
    }

    public Property<String> getMainModuleName() {
        return mainModuleName;
    }
    public void setMainModuleName(Property<String> mainModuleName) {
        this.mainModuleName = mainModuleName;
    }
    public void setMainModuleName(String mainModuleName) {
        this.mainModuleName.set(mainModuleName);
    }

    public Property<File> getBuildDir() {
        return buildDir;
    }
    public void setBuildDir(Property<File> buildDir) {
        this.buildDir = buildDir;
    }
    public void setBuildDir(File buildDir) {
        this.buildDir.set(buildDir);
    }

    public Property<String> getTargetModuleName() {
        return targetModuleName;
    }
    public void setTargetModuleName(Property<String> targetModuleName) {
        this.targetModuleName = targetModuleName;
    }
    public void setTargetModuleName(String targetModuleName) {
        this.targetModuleName.set(targetModuleName);
    }

    public Property<Boolean> getDebug() {
        return debug;
    }
    public void setDebug(Property<Boolean> debug) {
        this.debug = debug;
    }
    public void setDebug(Boolean debug) {
        this.debug.set(debug);
    }

    public Property<Boolean> getOptimize() {
        return debug;
    }
    public void setOptimize(Property<Boolean> optimize) {
        this.optimize = optimize;
    }
    public void setOptimize(Boolean optimize) {
        this.optimize.set(optimize);
    }


    @TaskAction
    public void make() {
        final List<String> elmMakeCmd = new ArrayList<>();
        final String osName = System.getProperty("os.name");
        if (osName.startsWith("Windows")) {
            elmMakeCmd.add("cmd");
            elmMakeCmd.add("/c");
        }
        elmMakeCmd.add(executable.get());
        elmMakeCmd.add("make");
        elmMakeCmd.add(Paths.get(getSourceDir().get().getPath(), mainModuleName.get()).toString());
        elmMakeCmd.add("--output");
        elmMakeCmd.add(Paths.get(getBuildDir().get().getPath(), targetModuleName.get()).toString());
        if (debug.get() && optimize.get()) {
            throw new TaskExecutionException(
                    this,
                    new IllegalArgumentException(
                            "I cannot compile with 'optimize' and 'debug' at the same time.")
            );
        }
        if (debug.get()) {
            elmMakeCmd.add("--debug");
        }
        if (optimize.get()) {
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
                    .directory(new File(executionDir.get()))
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
                    "'elm make' failed; see the compiler error output for details (run gradle with '--info').");
        }
    }

}

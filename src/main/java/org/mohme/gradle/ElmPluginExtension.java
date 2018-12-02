package org.mohme.gradle;

import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

import java.io.File;

@SuppressWarnings("UnstableApiUsage")
public class ElmPluginExtension {
    Property<String> executable;
    Property<String> executionDir;

    Property<File> sourceDir;
    Property<String> mainModuleName;
    Property<String> targetModuleName;

    Property<File> buildDir;

    Property<Boolean> debug;
    Property<Boolean> optimize;


    public ElmPluginExtension(Project project) {
        final ObjectFactory objectFactory = project.getObjects();

        executable = objectFactory.property(String.class);
        executionDir = objectFactory.property(String.class);

        sourceDir = objectFactory.property(File.class);
        mainModuleName = objectFactory.property(String.class);

        buildDir = objectFactory.property(File.class);
        targetModuleName = objectFactory.property(String.class);

        debug = objectFactory.property(Boolean.class);
        optimize = objectFactory.property(Boolean.class);
    }


    // TODO set with properties?!
    // TODO try to omit the getters/setters
    public Property<String> getExecutable() {
        return executable;
    }
    public void setExecutable(String executable) {
        this.executable.set(executable);
    }

    public Property<String> getExecutionDir() {
        return executionDir;
    }
    public void setExecutionDir(String executionDir) {
        this.executionDir.set(executionDir);
    }

    public Property<File> getSourceDir() {
        return sourceDir;
    }
    public void setSourceDir(File sourceDir) {
        this.sourceDir.set(sourceDir);
    }

    public Property<String> getMainModuleName() {
        return mainModuleName;
    }
    public void setMainModuleName(String mainModuleName) {
        this.mainModuleName.set(mainModuleName);
    }

    public Property<File> getBuildDir() {
        return buildDir;
    }
    public void setBuildDir(File buildDir) {
        this.buildDir.set(buildDir);
    }

    public Property<String> getTargetModuleName() {
        return targetModuleName;
    }
    public void setTargetModuleName(String targetModuleName) {
        this.targetModuleName.set(targetModuleName);
    }

    public Property<Boolean> getDebug() {
        return debug;
    }
    public void setDebug(Boolean debug) {
        this.debug.set(debug);
    }

    public Property<Boolean> getOptimize() {
        return optimize;
    }
    public void setOptimize(Boolean optimize) {
        this.optimize.set(optimize);
    }

}

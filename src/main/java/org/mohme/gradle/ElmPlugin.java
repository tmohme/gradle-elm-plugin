package org.mohme.gradle;

import java.nio.file.Paths;

import org.gradle.api.Plugin;
import org.gradle.api.Project;


public class ElmPlugin implements Plugin<Project> {
    
    @Override
    public void apply(Project project) {
        project.getTasks().create("elmMake", ElmMakeTask.class, elmMakeTask -> {
            // configure task with defaults
            elmMakeTask.setSourceDir(Paths.get("src", "elm").toFile());
            elmMakeTask.setBuildDir(Paths.get(project.getBuildDir().getPath(), "elm").toFile());
            
            elmMakeTask.executable = "elm";
            elmMakeTask.executionDir = ".";
            elmMakeTask.mainModuleName = "Main.elm";
            elmMakeTask.targetModuleName = "elm.js";
            elmMakeTask.debug = false;
            elmMakeTask.optimize = false;
        });
    }
    
}

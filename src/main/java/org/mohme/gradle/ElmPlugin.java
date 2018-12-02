package org.mohme.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.nio.file.Paths;


public class ElmPlugin implements Plugin<Project> {
    private static final String DEFAULT_EXECUTABLE = "elm";
    private static final String DEFAULT_MAIN_MODULE = "Main.elm";
    private static final String DEFAULT_TARGET_MODULE = "elm.js";

    @Override
    public void apply(Project project) {
        final ElmPluginExtension elm = project.getExtensions().create("elm", ElmPluginExtension.class, project);

        elm.executable.set(DEFAULT_EXECUTABLE);
        elm.executionDir.set(".");

        elm.sourceDir.set(Paths.get("src", "elm").toFile());
        elm.mainModuleName.set(DEFAULT_MAIN_MODULE);

        elm.buildDir.set(Paths.get(project.getBuildDir().getPath(), "elm").toFile());
        elm.targetModuleName.set(DEFAULT_TARGET_MODULE);

        elm.debug.set(false);
        elm.optimize.set(false);


        project.getTasks().create("elmMake", ElmMakeTask.class, new Action<ElmMakeTask>() {
            public void execute(ElmMakeTask task) {
                // configure defaults
                task.setExecutable(elm.getExecutable());
                task.setExecutionDir(elm.getExecutionDir());

                task.setSourceDir(elm.getSourceDir());
                task.setMainModuleName(elm.getMainModuleName());

                task.setBuildDir(elm.getBuildDir());
                task.setTargetModuleName(elm.getTargetModuleName());

                task.setDebug(elm.getDebug());
                task.setOptimize(elm.getOptimize());
            }
        });
    }
    
}

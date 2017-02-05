package org.mohme.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class ElmPlugin implements Plugin<Project> {

  @Override
  void apply(Project project) {
    project.tasks.create('elmMake', ElmMakeTask)
  }

}

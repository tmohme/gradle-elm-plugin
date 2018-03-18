package org.mohme.gradle

import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

import java.nio.file.Paths

@CompileStatic
class ElmMakeTask extends DefaultTask {
  private Logger logger

  @Input String executable = "elm-make"
  @Input String executionDir = "."
  @Input String mainModuleName = 'Main.elm'
  @Input String targetModuleName = 'elm.js'
  @Input boolean confirm = true
  @Input boolean debug = false
  @Input boolean warn = false

  private File sourceDir = Paths.get('src', 'elm').toFile()
  @InputDirectory
  File getSourceDir() {
    sourceDir
  }
  void setSourceDir(File sourceDir) {
    this.sourceDir = sourceDir
  }

  private File buildDir = Paths.get("${project.buildDir.path}", 'elm').toFile()
  @OutputDirectory
  File getBuildDir() {
    buildDir
  }
  void setBuildDir(File buildDir) {
    this.buildDir = buildDir
  }

  ElmMakeTask() {
    setGroup('Build')
    setDescription('Run `elm-make`.')
  }

  @TaskAction
  make() {
    logger = project.logger

    final osName = ((String)System.properties['os.name'])
    String[] osSpecificPrefix = (osName.startsWith('Windows')) ? ['cmd', '/c'] : []
    String[] elmMakeCmd = osSpecificPrefix +
                          [executable,
                           Paths.get(getSourceDir().path, mainModuleName).toString(),
                           "--output",
                           Paths.get(getBuildDir().path, targetModuleName).toString()]
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
              .directory(project.file(executionDir))
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

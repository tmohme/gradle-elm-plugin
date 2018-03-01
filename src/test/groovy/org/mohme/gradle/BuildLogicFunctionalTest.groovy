package org.mohme.gradle

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import spock.lang.Specification
import java.nio.file.Files;
import java.nio.file.Paths;

class BuildLogicFunctionalTest extends Specification {

  final File testProjectDir = Files.createTempDirectory("testProjectDir").toFile();
  File buildFile
  File mainFile

  def setup() {
    buildFile = new File(testProjectDir, 'build.gradle')
    buildFile.createNewFile();

    Paths.get(testProjectDir.getAbsolutePath(), 'src', 'main', 'elm').toFile().mkdirs();
    mainFile = new File(testProjectDir, 'src/main/elm/Main.elm')
    mainFile.createNewFile();

    Paths.get(testProjectDir.getAbsolutePath(), 'build', 'elm').toFile().mkdirs();
  }

  /**
   * Replace escape all '\' characters with '\\'
   * @return Properly escaped string.
   */
  private String fixPath() {
    return testProjectDir.getAbsolutePath().replaceAll("[\\\\]", "\\\\\\\\")
  }


  def "can apply plugin"() {
    given:
    buildFile << """
      plugins {
        id 'org.mohme.gradle.elm-plugin'
      }
    """

    when:
    def result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments('tasks', '--all')
            .withPluginClasspath()
            .build()

    then:
    result.output.contains("elmMake")
  }


  def "run elmMake successfully"() {
    given:
    buildFile << """\
      plugins {
        id 'org.mohme.gradle.elm-plugin'
      }

      elmMake {
        executable = 'elm-make'
        sourceDir = file('src/main/elm')
        executionDir = new File('""" + fixPath() + """')
        buildDir = file("\${project.buildDir.path}/elm")
      }
    """.stripIndent()

    mainFile << """\
      import Html
      main = Html.text "hello, world!"
    """.stripIndent()

    when:
    def result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments('elmMake', '--stacktrace', '--info')
            .withPluginClasspath()
            .withDebug(true)
            .build()

    then:
    result.output.contains(":elmMake")
    result.task(":elmMake").outcome == TaskOutcome.SUCCESS

    def elmJs = testProjectDir.path + '/build/elm/elm.js'
    new File(elmJs).exists()
  }


  def "warnings break the build"() {
    given:
    buildFile << """\
      plugins {
        id 'org.mohme.gradle.elm-plugin'
      }

      elmMake {
        executable = 'elm-make'
        sourceDir = file('src/main/elm')
        executionDir = new File('""" + fixPath() + """')
        buildDir = file("\${project.buildDir.path}/elm")
        warn = true
      }
    """.stripIndent()

    mainFile << """\
      import Html
      main = Html.text "hello, world!"
    """.stripIndent()

    when:
    def result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments('elmMake', '--stacktrace', '--info')
            .withPluginClasspath()
            .withDebug(true)
            .buildAndFail()

    then:
    result.output.contains(":elmMake")
    result.task(":elmMake").outcome == TaskOutcome.FAILED
  }
}
package org.mohme.gradle

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class BuildLogicFunctionalTest extends Specification {
  @Rule
  final TemporaryFolder testProjectDir = new TemporaryFolder()
  File buildFile
  File mainFile

  def setup() {
    buildFile = testProjectDir.newFile('build.gradle')

    testProjectDir.newFolder('src', 'main', 'elm')
    mainFile = testProjectDir.newFile('src/main/elm/Main.elm')

    testProjectDir.newFolder('build', 'elm')
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
            .withProjectDir(testProjectDir.root)
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
        executionDir = '${testProjectDir.root.canonicalPath}'
        buildDir = file("\${project.buildDir.path}/elm")
      }
    """.stripIndent()

    mainFile << """\
      import Html
      main = Html.text "hello, world!"
    """.stripIndent()

    when:
    def result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments('elmMake', '--stacktrace', '--info')
            .withPluginClasspath()
            .withDebug(true)
            .build()

    then:
    result.output.contains(":elmMake")
    result.task(":elmMake").outcome == TaskOutcome.SUCCESS

    def elmJs = testProjectDir.root.path + '/build/elm/elm.js'
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
        executionDir = '${testProjectDir.root.canonicalPath}'
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
            .withProjectDir(testProjectDir.root)
            .withArguments('elmMake', '--stacktrace', '--info')
            .withPluginClasspath()
            .withDebug(true)
            .buildAndFail()

    then:
    result.output.contains(":elmMake")
    result.task(":elmMake").outcome == TaskOutcome.FAILED
  }
}
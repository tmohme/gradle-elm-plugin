package org.mohme.gradle

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.nio.file.Files

class BuildLogicFunctionalTest extends Specification {
  @Rule
  TemporaryFolder testProjectDir = new TemporaryFolder()
  File buildFile
  File localBuildCacheDirectory
  File elmDotJson
  File sourceDir
  File mainFile

  def setup() {
    // establish a fresh build cache for each test
    localBuildCacheDirectory = testProjectDir.newFolder('local-cache')
    testProjectDir.newFile('settings.gradle') << """
        buildCache {
            local {
                directory '${localBuildCacheDirectory.toURI()}'
            }
        }
    """
    buildFile = testProjectDir.newFile('build.gradle')

    elmDotJson = testProjectDir.newFile('elm.json')

    sourceDir = testProjectDir.newFolder('src', 'main', 'elm')
    mainFile = Files.createFile(sourceDir.toPath().resolve('Main.elm')).toFile()
  }


  def "can apply plugin"(String gradleVersion) {
    given:
    buildFile << """
      plugins {
        id 'org.mohme.gradle.elm-plugin'
      }
    """

    when:
    def result = GradleRunner.create()
            .withGradleVersion(gradleVersion)
            .withProjectDir(testProjectDir.root)
            .withArguments('tasks', '--all', '--stacktrace', '--info')
            .withPluginClasspath()
            .build()

    then:
    result.output.contains("elmMake")

    where:
    gradleVersion | _
    "6.0"         | _
    "5.6.4"       | _
    "5.5.1"       | _
    "5.4.1"       | _
    "5.3.1"       | _
    "5.2"         | _
    "5.1.1"       | _
  }


  def "run elmMake successfully with provided elm executable"(String gradleVersion) {
    given:
    buildFile << """\
      import org.mohme.gradle.Executable
      
      plugins {
        id 'org.mohme.gradle.elm-plugin'
      }
      
      elmMake {
        executable = Executable.Provided.INSTANCE

        executionDir = '${testProjectDir.root.canonicalPath}'
        sourceDir = file('src/main/elm')
      }
    """.stripIndent()

    elmDotJson << elmDotJsonContent
    mainFile << mainFileContent

    when:
    def result = GradleRunner.create()
            .withGradleVersion(gradleVersion)
            .withProjectDir(testProjectDir.root)
            .withArguments('elmMake', '--stacktrace', '--info')
            .withPluginClasspath()
            .withDebug(true)
            .build()

    then:
    result.task(":elmMake").outcome == TaskOutcome.SUCCESS

    def elmJs = testProjectDir.root.path + '/build/elm/elm.js'
    new File(elmJs).exists()


    where:
    gradleVersion | _
    "6.0"         | _
    "5.6.4"       | _
    "5.5.1"       | _
    "5.4.1"       | _
    "5.3.1"       | _
    "5.2"         | _
    "5.1.1"       | _
  }

  def "run elmMake successfully with downloaded elm executable"(String gradleVersion) {
    given:
    buildFile << """\
      import org.mohme.gradle.Executable
      
      plugins {
        id 'org.mohme.gradle.elm-plugin'
      }
      
      elmMake {
        executable = Executable.Download.V_0_19_1.INSTANCE

        executionDir = '${testProjectDir.root.canonicalPath}'
        sourceDir = file('src/main/elm')
      }
    """.stripIndent()

    elmDotJson << elmDotJsonContent
    mainFile << mainFileContent

    when:
    def result = GradleRunner.create()
            .withGradleVersion(gradleVersion)
            .withProjectDir(testProjectDir.root)
            .withArguments('elmMake', '--stacktrace', '--info')
            .withPluginClasspath()
            .withDebug(true)
            .build()

    then:
    result.task(":elmMake").outcome == TaskOutcome.SUCCESS

    def elmJs = testProjectDir.root.path + '/build/elm/elm.js'
    new File(elmJs).exists()


    where:
    gradleVersion | _
    "6.0"         | _
    "5.6.4"       | _
    "5.5.1"       | _
    "5.4.1"       | _
    "5.3.1"       | _
    "5.2"         | _
    "5.1.1"       | _
  }


  def "run elmMake successfully with default configuration (mostly)"(String gradleVersion) {
    given:
    buildFile << minimalGroovyBuildFileContent()
    elmDotJson << elmDotJsonDefaultContent
    sourceDir = testProjectDir.newFolder('src', 'elm')
    mainFile = Files.createFile(sourceDir.toPath().resolve('Main.elm')).toFile()
    mainFile << mainFileContent

    when:
    def result = GradleRunner.create()
            .withGradleVersion(gradleVersion)
            .withProjectDir(testProjectDir.root)
            .withArguments('elmMake', '--stacktrace', '--info')
            .withPluginClasspath()
            .withDebug(true)
            .build()

    then:
    result.task(":elmMake").outcome == TaskOutcome.SUCCESS

    def elmJs = testProjectDir.root.path + '/build/elm/elm.js'
    new File(elmJs).exists()

    where:
    gradleVersion | _
    "6.0"         | _
    "5.6.4"       | _
    "5.5.1"       | _
    "5.4.1"       | _
    "5.3.1"       | _
    "5.2"         | _
    "5.1.1"       | _
  }


  def "run elmMake successfully with task configuration"(String gradleVersion) {
    given:
    buildFile << """\
      import java.nio.file.Paths
      import org.mohme.gradle.Executable
      
      plugins {
        id 'org.mohme.gradle.elm-plugin'
      }
      
      elmMake {
        executable = Executable.Provided.INSTANCE
        executionDir = '${testProjectDir.root.canonicalPath}'

        sourceDir = file('src/main/elm')
        mainModuleName = 'Main.elm'
        
        buildDir = Paths.get("\${project.buildDir.path}", 'elm').toFile()
        targetModuleName = 'elm.js'
        
        debug = false
        optimize = true
      }
    """.stripIndent()

    elmDotJson << elmDotJsonContent
    mainFile << mainFileContent

    when:
    def result = GradleRunner.create()
            .withGradleVersion(gradleVersion)
            .withProjectDir(testProjectDir.root)
            .withArguments('elmMake', '--stacktrace', '--info')
            .withPluginClasspath()
            .withDebug(true)
            .build()

    then:
    result.task(":elmMake").outcome == TaskOutcome.SUCCESS

    def elmJs = testProjectDir.root.path + '/build/elm/elm.js'
    new File(elmJs).exists()

    where:
    gradleVersion | _
    "6.0"         | _
    "5.6.4"       | _
    "5.5.1"       | _
    "5.4.1"       | _
    "5.3.1"       | _
    "5.2"         | _
    "5.1.1"       | _
  }


  def "run elmMake successfully with extension configuration"() {
    given:
    buildFile << """\
      import java.nio.file.Paths
      import org.mohme.gradle.Executable
      
      plugins {
        id 'org.mohme.gradle.elm-plugin'
      }
      
      elm {
        executable = Executable.Provided.INSTANCE
        executionDir = '${testProjectDir.root.canonicalPath}'

        sourceDir = file('src/main/elm')
        mainModuleName = 'Main.elm'
        
        buildDir = file("\${project.buildDir.path}/elm")
        targetModuleName = 'elm.js'
        
        debug = true
        optimize = false
      }
    """.stripIndent()

    elmDotJson << elmDotJsonContent
    mainFile << mainFileContent

    when:
    def result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments('elmMake', '--stacktrace', '--info')
            .withPluginClasspath()
            .withDebug(true)
            .build()

    then:
    result.task(":elmMake").outcome == TaskOutcome.SUCCESS

    def elmJs = testProjectDir.root.path + '/build/elm/elm.js'
    new File(elmJs).exists()
  }

  def "elmMake is loaded from cache"() {
    given:
    buildFile << minimalGroovyBuildFileContent()
    elmDotJson << elmDotJsonDefaultContent
    sourceDir = testProjectDir.newFolder('src', 'elm')
    mainFile = Files.createFile(sourceDir.toPath().resolve('Main.elm')).toFile()
    mainFile << mainFileContent

    when:
    def fresh = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments('elmMake', '--build-cache', '--info')
            .withPluginClasspath()
            .withDebug(true)
            .build()

    then:
    fresh.task(":elmMake").outcome == TaskOutcome.SUCCESS

    when:
    new File(testProjectDir.root, 'build').deleteDir()
    def cached = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments('elmMake', '--build-cache', '--info')
            .withPluginClasspath()
            .withDebug(true)
            .build()

    then:
    cached.task(":elmMake").outcome == TaskOutcome.FROM_CACHE
  }


  def "logs stdout output with level 'info'"() {
    given:
    buildFile << minimalGroovyBuildFileContent()
    elmDotJson << elmDotJsonDefaultContent
    sourceDir = testProjectDir.newFolder('src', 'elm')
    mainFile = Files.createFile(sourceDir.toPath().resolve('Main.elm')).toFile()
    mainFile << mainFileContent

    when:
    def result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments('elmMake', '--info')
            .withPluginClasspath()
            .withDebug(true)
            .build()

    then:
    result.task(":elmMake").outcome == TaskOutcome.SUCCESS
    result.output.contains("Success! Compiled 1 module.")
  }


  def "logs stderr output when no log level is given on CLI"() {
    given:
    buildFile << minimalGroovyBuildFileContent()
    elmDotJson << elmDotJsonDefaultContent
    sourceDir = testProjectDir.newFolder('src', 'elm')
    mainFile = Files.createFile(sourceDir.toPath().resolve('Main.elm')).toFile()
    mainFile << "this is no valid elm code"

    when:
    def result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments('elmMake')
            .withPluginClasspath()
            .withDebug(true)
            .buildAndFail()

    then:
    result.task(":elmMake").outcome == TaskOutcome.FAILED
    result.output.contains("-- PROBLEM IN DEFINITION --")
  }


  def minimalGroovyBuildFileContent() {
    // use a method to delay resolution of '${testProjectDir.root.canonicalPath}' until 'testProjectDir' is defined
    return """\
      import java.nio.file.Paths
      
      plugins {
        id 'org.mohme.gradle.elm-plugin'
      }
      
      elm {
        executionDir = '${testProjectDir.root.canonicalPath}'
      }
    """.stripIndent()
  }


  def mainFileContent = """\
      module Main exposing (main)
      import Html 
      main = Html.text "hello, world!"
    """.stripIndent()

  def elmDotJsonContent = """\
    {
        "type": "application",
        "source-directories": [
            "src/main/elm"
        ],
        "elm-version": "0.19.1",
        "dependencies": {
            "direct": {
                "elm/core": "1.0.0",
                "elm/html": "1.0.0"
            },
            "indirect": {
                "elm/json": "1.0.0",
                "elm/virtual-dom": "1.0.0"
            }
        },
        "test-dependencies": {
            "direct": {},
            "indirect": {}
        }
    }
    """.stripIndent()

  def elmDotJsonDefaultContent = elmDotJsonContent.replaceAll("src/main/elm", "src/elm")
}

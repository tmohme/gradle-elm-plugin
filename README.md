[![Build Status](https://travis-ci.org/tmohme/gradle-elm-plugin.svg?branch=master)](https://travis-ci.org/tmohme/gradle-elm-plugin)

# gradle-elm-plugin
A gradle plugin for convenient use of elm.

This plugin requires a working installation of the elm-platform in version **0.19**.  
If you want to use elm-platform **0.18**, please use version **1.0.0** of this plugin.

## Prerequisites
Obviously you need a working `elm` installation to use this plugin.  
When you want to use the `elmTest` task, you need a working installation of the [node-test-runner `elm-test`](https://github.com/rtfeldman/node-test-runner).

## Usage
Apply the plugin using standard Gradle procedure.  
For Groovy:

```groovy
plugins {
    id 'org.mohme.gradle.elm-plugin' version '<current version>'
}
```

For Kotlin:
```kotlin
plugins {
  id("org.mohme.gradle.elm-plugin" ) version "<current version>"
}

```

## Extension
The plugin supports the `elm` extension with the following properties:

| Name               | default                        | type    | description |
| ------------------ | ------------------------------ | ------- | ----------- |
| `buildDir`         | `${project.buildDir.path}/elm` | File    | The name of the directory in which we place the output. |
| `debug`            | `true`                         | boolean | Determines whether `elm make` will run with the the `--debug`-flag. | 
| `executable`       | `elm`                          | String  | The name of the executable to use. |
| `executionDir`     | `.`                            | String  | The name of the working directory for the execution of `elm-make`. |
| `mainModuleName`   | `Main.elm`                     | String  | The name of the main module to give to elm-make. |
| `optimize`         | `true`                         | boolean | Determines whether `elm make` will run with the the `--optimize`-flag. | 
| `sourceDir`        | `src/elm`                      | File    | The name of the directory in/below which the elm source files are contained. |
| `targetModuleName` | `elm.js`                       | String  | The name of the output file to produce. |
| `testExecutable`   | `elm-test`                     | String  | The name of the executable to use. |
| `testExecutionDir` | `.`                            | String  | The name of the working directory for the execution of `elm-test`. |

Groovy example:
```groovy
elm {
    sourceDir = file('src/main/elm')
    targetModuleName = 'main.js'
    debug = true
    optimize = false
}
```

Kotlin example:
```kotlin
elm {
    sourceDir.set(project.file("src/main/elm"))
    targetModuleName.set("main.js")
    debug.set(true)
    optimize.set(false)
}
```

## Tasks
### elmMake
This plugin adds a `elmMake` task to the build.  
It *does not* make any other task depend on `elmMake`, thus you might want to add such a dependency yourself.

The task has the same configurable properties as the above mentioned `elm` extension (except for the "test*" properties).  
When the property on the task is not set, it defaults to the value  of the corresponding property of the `elm` 
extension.

### elmTest
This plugin adds a `elmTest` task to the build.  
It *does not* make any other task depend on `elmTest`, thus you might want to add such a dependency yourself.

The tasks only configurable properties are `executable` and `executionDir`, which correspond to the `elm` extension 
properties `testExecutable` resp. `testExecutionDir`.

Effectively, the `elmTest` task is nothing more than a simple mechanism to integrate the `node-test-runner` into the 
build. As such the same restrictions and assumptions apply. The most obvious one is, that the tests have to be placed 
in a directory named `tests` immediately below the execution directory.  
For other restrictions / assumptions please have a look at [it's documentation](https://github.com/rtfeldman/node-test-runner).
If you need more configurability, please let me know.

## Compatibility
This plugin is versioned according to the [Semantic Versioning](https://semver.org) rules.
* Versions >=3.2.0 are compatible with elm 0.19 and Gradle >= 4.4.1  (tested with 4.4.1 .. 5.1.1)
* Versions 2.0.3 .. 3.1.2 are compatible with elm 0.19 and Gradle 5.x (tested with 5.0 .. 5.1.1)
* Versions 2.0.0 .. 2.0.2 are compatible with elm 0.19 and Gradle 4.10
* Version 1.0.0 is compatible with elm 0.18 and Gradle 4.6

## Release and Migration notes
. . . can be found [here](release-migration-notes.md)

## Known problems
Running `elm` might be [incredibly slow](https://github.com/elm-lang/elm-compiler/issues/1473) on some CI-platforms.  
This is not a problem of this plugin, but kind of a misunderstanding between `elm make` and what the underlying platform
tells the elm compiler about the platform's capabilities.  
Be careful with the workarounds that are described in various discussions (e.g. in 
[this elm-discuss thread](https://groups.google.com/forum/#!topic/elm-discuss/Y3bTYRPqBXE)). They typically wrap the
original `elm` executable in a small script (which is OK) without ensuring that the script returns with `elm`'s original 
return code (which causes trouble).  
When you are using this kind of wrapper-script and have trouble with this plugin, have a look at this project's 
[`.travis.yml`](.travis.yml).  

## Contribution
I'm happy about contributions of all sorts.  
Before you actually do work on the project, please read the [contribution guide](contributing.md).

## License
This plugin is made available under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0).

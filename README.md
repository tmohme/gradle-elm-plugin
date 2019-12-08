[![Build Status](https://travis-ci.org/tmohme/gradle-elm-plugin.svg?branch=master)](https://travis-ci.org/tmohme/gradle-elm-plugin)

# gradle-elm-plugin 4.0.0-SNAPSHOT
A gradle plugin for convenient use of elm.

If you are looking for documentation for a different version, please go to the list of commits and browse the 
repository at the corresponding point in the history.

This plugin can be used with elm **0.19**.  
If you want to use elm-platform **0.18**, please use version **1.0.0** of this plugin.

## Prerequisites
The plugin can use a provided `elm` installation or download a platform-specific version on the fly. 
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

If you are confused about what the tasks are doing, try running gradle with `--info`. Then the tasks will produce log
output showing the actual call to the underlying functionality.

## Extension
The plugin supports the `elm` extension with the following properties:

| Name               | default                        | type                        | description |
| ------------------ | ------------------------------ | ----------------------------| ----------- |
| `buildDir`         | `${project.buildDir.path}/elm` | File                        | The name of the directory in which we place the output. |
| `debug`            | `true`                         | boolean                     | Determines whether `elm make` will run with the `--debug`-flag. | 
| `executable`       | Provided                       | org.mohme.gradle.Executable | The executable to use.|
| `executionDir`     | `.`                            | String                      | The name of the working directory for the execution of `elm make`. |
| `mainModuleName`   | `Main.elm`                     | String                      | The name of the main module to give to `elm make`. |
| `optimize`         | `true`                         | boolean                     | Determines whether `elm make` will run with the the `--optimize`-flag. | 
| `sourceDir`        | `src/elm`                      | File                        | The name of the directory in which the main module will be searched. |
| `targetModuleName` | `elm.js`                       | String                      | The name of the output file to produce. |
| `testExecutable`   | `elm-test`                     | String                      | The name of the executable to use. |
| `testExecutionDir` | `.`                            | String                      | The name of the working directory for the execution of `elm-test`. |

### Special types
#### org.mohme.gradle.Executable
The property `executable` accepts values of type `org.mohme.gradle.Executable` for which there are multiple occurrences:
* `org.mohme.gradle.Executable.Provided` - specifies that the `elm` executable is provided by a local installation and 
  reachable via the $PATH under the name `elm`.
* `org.mohme.gradle.Executable.Download.V_0_19_0` - specifies to download and use the elm-compiler in the corresponding 
  version. The downloaded artifact will be store in the `./build/gradle-elm` directory for future reuse. 
* `org.mohme.gradle.Executable.Download.V_0_19_1` - specifies to download and use the elm-compiler in the corresponding 
  version. The downloaded artifact will be store in the `./build/gradle-elm` directory for future reuse. 
  
Of course you can use regular `import` statements to avoid littering your build-file with fully qualified class names.
By using dedicated classes/objects, at least Kotlin build script users get the advantage of editor-support for
auto-completion. Also it is impossible to specify an unsupported version for download.

For the download there's a connect timeout of 10s and a read timeout of 30s.

### Groovy example:
```groovy
elm {
    sourceDir = file('src/main/elm')
    executable = org.mohme.gradle.Executable.Provided.INSTANCE
    targetModuleName = 'main.js'
    debug = true
    optimize = false
}
```

### Kotlin example:
```kotlin
elm {
    sourceDir.set(project.file("src/main/elm"))
    executable = org.mohme.gradle.Executable.Provided
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

#### Expected directory layout
This task is just a thin wrapper around the `elm` executable and as such "inherits" all assumptions/restrictions
about the directory layout of the project:
* The `elm.json` file is expected in the current working directory, which can be specified as `executionDir` 
  (see "Extension" above).
* Within the `elm.json`, you have to specify `source-directories` in which `elm` will look for modules.
* The task property `sourceDir` _only_ impacts the creation of the full path to the mainModule for the
  underlying "elm make" call.

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
* Versions >=4.0.0 are compatible with elm 0.19 and Gradle >=5.1 (tested with 5.1.1 .. 6.0)
* Version  3.3.0 is compatible with elm 0.19 and Gradle 4.9 .. 5.6.4
* Versions 3.2.0 .. 3.2.2 are compatible with elm 0.19 and Gradle 4.4.1 .. 5.6
* Versions 2.0.3 .. 3.1.2 are compatible with elm 0.19 and Gradle 5.0 .. 5.1.1
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

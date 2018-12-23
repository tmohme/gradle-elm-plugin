[![Build Status](https://travis-ci.org/tmohme/gradle-elm-plugin.svg?branch=master)](https://travis-ci.org/tmohme/gradle-elm-plugin)

# gradle-elm-plugin
A gradle plugin for convenient use of elm.

This plugin requires a working installation of the elm-platform in version **0.19**.  
If you want to use elm-platform **0.18**, please use version **1.0.0** of this plugin.


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

| Name           | default                        | type    | description |
| -------------- | ------------------------------ | ------- | ----------- |
| `buildDir`     | `${project.buildDir.path}/elm` | File    | The name of the directory in which we place the output. |
| `debug`        | `true`                         | boolean | Determines whether `elm make` will run with the the `--debug`-flag. | 
| `executable`   | `elm`                          | String  | The name of the executable to use. |
| `executionDir` | `.`                            | File    | The name of the working directory for the execution of `elm-make`. |
| `mainModule`   | `Main.elm`                     | String  | The name of the main module to give to elm-make. |
| `optimize`     | `true`                         | boolean | Determines whether `elm make` will run with the the `--optimize`-flag. | 
| `sourceDir`    | `src/elm`                      | File    | The name of the directory in/below which the elm source files are contained. |
| `targetModule` | `elm.js`                       | String  | The name of the output file to produce. |

Groovy example:
```groovy
elm {
    srcDir = file('src/main/elm')
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
This plugin adds a `elmMake` task to the build.  
It *does not* make any other task depend on `elmMake`, thus you might want to add such a dependency yourself.

The task has the same configurable properties as the above mentioned `elm` extension.  
When the property on the task is not set, it defaults to the value  of the corresponding property of the `elm` 
extension.

## Compatibility
The plugin is tested with elm 0.19 and Gradle [4.10, 5.0].

## Release and Migration notes
. . . can be found [here](release-migration-notes.md)

## Known problems
Running `elm`  might be [incredibly slow](https://github.com/elm-lang/elm-compiler/issues/1473) on some CI-platforms.  
This is not a problem of this plugin, but kind of a misunderstanding between `elm make` and what the underlying platform
tells `elm` about its capabilities.  
Workarounds are described in the linked discussion and e.g. in [this elm-discuss thread](https://groups.google.com/forum/#!topic/elm-discuss/Y3bTYRPqBXE).  

## Contribution
I'm happy about contributions of all sorts.  
Before you actually do work on the project, please read the contribution guide (`contributing.md`).

## License
This plugin is made available under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0).

[![Build Status](https://travis-ci.org/tmohme/gradle-elm-plugin.svg?branch=master)](https://travis-ci.org/tmohme/gradle-elm-plugin)

# gradle-elm-plugin
A gradle plugin for convenient use of elm.

This plugin requires a working installation of the elm-platform in version 0.18.  
Other versions might also work, but are not tested.


## Usage
Apply the plugin using standard Gradle procedure:

```groovy
plugins {
    id 'org.mohme.gradle.elm-plugin' version '<current version>'
}
```


## Tasks
This plugin adds a `elmMake` task to the build.  
It *does not* make any other task depend on `elmMake`, thus you might want to add such a dependency yourself.

Configurable properties of the task:

| Name           | default                        | type    | description |
| -------------- | ------------------------------ | ------- | ----------- |
| `buildDir`     | `${project.buildDir.path}/elm` | File    | The name of the directory in which we place the output. |
| `confirm`      | `true`                         | boolean | Determines whether `elm-make` actions will automatically get confirmed, or not. Translates to the `--yes`-flag. |
| `debug`        | `true`                         | boolean | Determines whether `elm-make` will run with the the `--debug`-flag. | 
| `executable`   | `elm-make`                     | String  | The name of the executable to use. |
| `executionDir` | `.`                            | File    | The name of the working directory for the execution of `elm-make`. |
| `mainModule`   | `Main.elm`                     | String  | The name of the main module to give to elm-make. |
| `sourceDir`    | `src/elm`                      | File    | The name of the directory in/below which the elm source files are contained. |
| `targetModule` | `elm.js`                       | String  | The name of the output file to produce. |
| `warn`         | `false`                        | boolean | Determines whether `elm-make` will run with the `--warn`-flag. <br/> **Attention:** Warnings will break the build!|

## Compatibility
The plugin is tested with elm 0.18 and Gradle [3.3, 4.6].

## Known problems
Running `elm-make`  might be [incredibly slow](https://github.com/elm-lang/elm-compiler/issues/1473) on some CI-platforms.  
This is not a problem of this plugin, but kind of a misunderstanding between `elm-make` and what the underlying platform
tells `elm-make` about its capabilities.  
Workarounds are described in the linked discussion and e.g. in [this elm-discuss thread](https://groups.google.com/forum/#!topic/elm-discuss/Y3bTYRPqBXE).  

## Contribution
I'm happy about contributions of all sorts.  
Before you actually do work on the project, please read the contribution guide (`contributing.md`).

## License
This plugin is made available under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0).

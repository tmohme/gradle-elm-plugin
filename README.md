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

* `executable`  
  The name of the executable to use.  
  Defaults to "elm-make".
* `executionDir`  
  The working directory for the execution of elm-make.  
  Defaults to ".".
* `sourceDir`  
  The directory in/below which the elm source files are contained.  
  Defaults to 'src/elm'.
* `mainModule`  
  The name of the main module to give to elm-make.
  Defaults to 'Main.elm'.
* `buildDir`  
  The director in which we place the output. 
  Defaults to "${project.buildDir.path}/elm"
* `targetModule`  
  The ouput file to produce.
  Defaults to 'elm.js'
* `confirm`  
  Determines whether 'elm-make' actions will automatically get confirmed, or not.  
  Defaults to `true`.
* `debug`
  Determines whether 'elm-make' will run with the the `--debug`-flag.  
  Defaults to `true`.


## License
This plugin is made available under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0).

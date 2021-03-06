# Release and Migration notes

## 4.0.1
* Unpack downloaded elm executable only if required.

## 4.0.0
* Add support for Gradle 6.0, 
* **Breaking change:** Remove support for Gradle 4.x because its API is incompatible with 6.x
* **Breaking change:** Remove support for Gradle 5.0 because its API lacks required functions.
* **Breaking change:** Property `executable` gets specified via dedicated types instead of a String.

## 3.3.0
* Upgrade to Gradle 5.6.4.
* Remove support for gradle < 4.9 because theses gradle versions have problems with openjdk 11.0.5

## 3.2.3
* Upgrade to Gradle 5.6.

## 3.2.2
* Improve documentation.

## 3.2.1
* Fix documentation issues.

## 3.2.0
* Improve compatibility with older Gradle versions.  
  The plugin is now tested against all minor versions (latest patch) of gradle Gradle since 4.4.1 

## 3.1.2
* Fix documentation issues.

## 3.1.1
* Fix documentation issues.

## 3.1.0
* Add (very) basic support for `elm-test`
* Removed (previously undocumented) possibilities to set the `elm` extension properties. 
  Now only the documented ones still exist.   

## 3.0.0
* Distinguish success vs. failure of `elm make` based on the process' exit code instead of content in `stderr` 
  (as it was required for elm 0.18.0)
* Log `elm make` _stderr_ output with log level `error` in gradle build.
  `elm make` _stdout_ output continues to be logged with log level `info` in gradle build. 

## 2.2.0
The `elmMake` task is now cacheable.

## 2.1.0
* The plugin got converted to Kotlin, thus usage in a Kotlin build script should be no problem.  
  Usage in a Groovy build script should be unaffected.
* Introduced the `elm` extension for a more declarative configuration and/or configuration of same values for 
  multiple `elmMake` tasks.

## 1.0.0 -> 2.0.0
* Starting with version 2.0.0 we internally expect elm version 0.19.0, so you obviously must have this 
  elm version installed.
* `elmMake` no longer supports the `warn` property as it is not supported by elm 0.19.0.
* `elmMake` supports the new property `optimize` which gets passed thru to the elm executable.

## 0.2.1 -> 1.0.0
* `elmMake`'s  properties `buildDir` and `srcDir` now are of type `File` instead of `String`.
  Replacing the current `'some/path'` with `project.file('some/path')` should be all that is needed.
* `elmMake`'s properties `mainModule` and `targetModule` have been renamed to `mainModuleName` resp. 
  `targetModuleName`. 

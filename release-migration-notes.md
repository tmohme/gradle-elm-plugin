# Release and Migration notes

## 2.0.0 -> 2.1.0
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

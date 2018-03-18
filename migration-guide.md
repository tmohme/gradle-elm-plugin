# Migrations for your plugin usage

## 0.2.1 -> 1.0.0
* `elmMake`'s  properties `buildDir` and `srcDir` now are of type `File` instead of `String`.
  Replacing the current `'some/path'` with `project.file('some/path')` should be all that is needed.
* `elmMake`'s properties `mainModule` and `targetModule` have been renamed to `mainModuleName` resp. `targetModuleName`. 

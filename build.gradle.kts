plugins {
  groovy
  `kotlin-dsl`
  `maven-publish`
  id("net.researchgate.release") version "2.8.1"
  id("com.gradle.plugin-publish") version "0.10.1"
  id("com.github.ben-manes.versions") version "0.22.0"
}

group = "org.mohme.gradle"

repositories {
  jcenter()
}

gradlePlugin {
  plugins {
      create("elmPlugin", delegateClosureOf<PluginDeclaration> {
          id = "org.mohme.gradle.elm-plugin"
          implementationClass = "org.mohme.gradle.ElmPlugin"
      })
  }
}

dependencies {
  testCompile( gradleTestKit() )
  testCompile("org.spockframework:spock-core:1.3-groovy-2.5") {
    exclude(module = "groovy-all")
  }
}

publishing {
  publications {
    register<MavenPublication>("maven") {
      artifactId = "elm-plugin"
      from(components["java"])
    }
  }
}

pluginBundle {
  website = "https://github.com/tmohme/gradle-elm-plugin"
  vcsUrl = "https://github.com/tmohme/gradle-elm-plugin"
  description = "A gradle plugin for convenient use of elm."
  tags = listOf("elm", "elm-make", "elm make")

  plugins {
      get("elmPlugin").displayName = "Gradle Elm Plugin"
  }
}

// To create a new release simply run "./gradlew release"
tasks.afterReleaseBuild.get().dependsOn(tasks.publishPlugins)

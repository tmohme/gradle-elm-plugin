plugins {
    groovy
    `kotlin-dsl`
    `maven-publish`
    kotlin("jvm") version "1.3.50"
    id("com.github.ben-manes.versions") version "0.27.0"
    id("com.gradle.plugin-publish") version "0.10.1"
    id("io.gitlab.arturbosch.detekt").version("1.1.1")
    id("net.researchgate.release") version "2.8.1"
}

group = "org.mohme.gradle"

repositories {
    jcenter()
}

detekt {
    buildUponDefaultConfig = true
    config = files("detekt.config.yml")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
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
    implementation("com.github.kittinunf.fuel:fuel:2.2.1")

    testImplementation(gradleTestKit())
//    testImplementation("io.mockk:mockk:1.9")
    testImplementation("io.strikt:strikt-core:0.22.3")
    testImplementation("org.spockframework:spock-core:1.3-groovy-2.5") {
        exclude(module = "groovy-all")
    }
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.2")
}

val kotlinVersion = "1.3.50"
configurations.all {
    resolutionStrategy {
        failOnVersionConflict()
        dependencySubstitution {
            // kotlin-stdlib
            substitute(module("org.jetbrains.kotlin:kotlin-stdlib:1.3.30"))
                    .with(module("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"))
            substitute(module("org.jetbrains.kotlin:kotlin-stdlib:1.3.60"))
                    .with(module("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"))

            // kotlin-stdlib-jdk8
            substitute(module("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.41"))
                    .with(module("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"))
            substitute(module("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.60"))
                    .with(module("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"))

            // kotlin-reflect
            substitute(module("org.jetbrains.kotlin:kotlin-reflect:1.3.0"))
                    .with(module("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion"))
            substitute(module("org.jetbrains.kotlin:kotlin-reflect:1.3.41"))
                    .with(module("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion"))
            substitute(module("org.jetbrains.kotlin:kotlin-reflect:1.3.60"))
                    .with(module("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion"))
        }
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

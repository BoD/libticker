allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
    }

    group = "org.jraf"
    version = "1.5.1"
}

plugins {
    kotlin("jvm").apply(false)
}

// Run `./gradlew refreshVersions` to update dependencies

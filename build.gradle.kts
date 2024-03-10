allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
    }

    group = "org.jraf"
    version = "1.6.0"
}

plugins {
    kotlin("jvm").apply(false)
}

// Run `./gradlew refreshVersions` to update dependencies

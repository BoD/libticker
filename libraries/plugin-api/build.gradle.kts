plugins {
    kotlin("jvm")
    id("maven-publish")
    id("org.jetbrains.dokka")
}

tasks {
    // Generate Javadoc (Dokka) Jar
    register<Jar>("dokkaHtmlJar") {
        archiveClassifier.set("javadoc")
        from(layout.buildDirectory.get().dir("dokka"))
        dependsOn(dokkaHtml)
    }

    // Generate Sources Jar
    register<Jar>("sourcesJar") {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
            artifact(tasks.getByName("dokkaHtmlJar"))
            artifact(tasks.getByName("sourcesJar"))
        }
    }
}

dependencies {
}

// Run `./gradlew publishToMavenLocal` to publish to the local maven repo

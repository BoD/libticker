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
    // Nanohttpd
    implementation("org.nanohttpd:nanohttpd:2.3.1")

    // Kotlinx Html
    implementation("org.jetbrains.kotlinx:kotlinx-html:0.9.1")

    // Core
    implementation(project(":libticker-core"))

}

// Run `./gradlew publishToMavenLocal` to publish to the local maven repo

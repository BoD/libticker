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
    // Plugin api
    implementation(project(":libticker-plugin-api"))

    // Slf4j
    implementation("org.slf4j:slf4j-api:1.7.30")

    // Rx
    implementation("io.reactivex.rxjava2:rxjava:2.2.20")
    implementation("io.reactivex.rxjava2:rxkotlin:2.4.0")

    // Coroutines
    implementation(KotlinX.coroutines.jdk9)

    // Twitter4J
    implementation("org.twitter4j:twitter4j-core:4.0.7")

    // ForecasIO
    implementation("com.github.dvdme:ForecastIOLib:1.6.0")

    // Sunrise sunset
    implementation("ca.rmen:lib-sunrise-sunset:1.1.1")

    // FRC
    implementation("ca.rmen:lib-french-revolutionary-calendar:1.8.2")

    // Klaxon
    implementation("com.beust:klaxon:5.4")

    // klibappstorerating
    implementation("org.jraf:klibappstorerating:1.1.1")

    // Google photos
    implementation("com.google.photos.library:google-photos-library-client:1.6.0")
    implementation("com.google.api-client:google-api-client:1.31.0")
}

// Run `./gradlew publishToMavenLocal` to publish to the local maven repo

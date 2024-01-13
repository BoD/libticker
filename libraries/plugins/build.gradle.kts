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
    implementation("org.slf4j:slf4j-api:_")

    // Rx
    implementation(ReactiveX.rxJava2)
    implementation(ReactiveX.rxJava2.rxKotlin)

    // Coroutines
    implementation(KotlinX.coroutines.jdk9)

    // Twitter4J
    implementation("org.twitter4j:twitter4j-core:_")

    // ForecasIO
    implementation("com.github.dvdme:ForecastIOLib:_")

    // Sunrise sunset
    implementation("ca.rmen:lib-sunrise-sunset:_")

    // FRC
    implementation("ca.rmen:lib-french-revolutionary-calendar:_")

    // Klaxon
    implementation("com.beust:klaxon:_")

    // klibappstorerating
    implementation("org.jraf:klibappstorerating:_")

    // Google photos
    implementation("com.google.photos.library:google-photos-library-client:_")
    implementation("com.google.api-client:google-api-client:_")
}

// Run `./gradlew publishToMavenLocal` to publish to the local maven repo

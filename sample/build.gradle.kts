plugins {
    kotlin("jvm")
    id("application")
}

application {
    mainClass = "org.jraf.libticker.sample.SampleKt"
}

dependencies {
    // Slf4j
    implementation("org.slf4j:slf4j-simple:1.7.30")

    // Rx
    implementation("io.reactivex.rxjava2:rxjava:2.2.20")
    implementation("io.reactivex.rxjava2:rxkotlin:2.4.0")

    // Core
    implementation(project(":libticker-core"))

    // Plugins
    implementation(project(":libticker-plugins"))

    // Http conf
    implementation(project(":libticker-httpconf"))
}

// Run "./gradlew run" to run the sample

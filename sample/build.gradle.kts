plugins {
    kotlin("jvm")
    id("application")
}

application {
    mainClass = "org.jraf.libticker.sample.SampleKt"
}

dependencies {
    // Slf4j
    implementation("org.slf4j:slf4j-simple:_")

    // Rx
    implementation(ReactiveX.rxJava2)
    implementation(ReactiveX.rxJava2.rxKotlin)

    // Core
    implementation(project(":libticker-core"))

    // Plugins
    implementation(project(":libticker-plugins"))

    // Http conf
    implementation(project(":libticker-httpconf"))
}

// Run "./gradlew run" to run the sample

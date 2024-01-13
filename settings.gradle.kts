plugins {
    // See https://jmfayard.github.io/refreshVersions
    id("de.fayard.refreshVersions") version "0.60.3"
}

// Include all the sample modules from the "samples" directory
file("libraries").listFiles()!!.forEach { dir ->
    include(dir.name)
    project(":${dir.name}").apply {
        projectDir = dir
        name = "libticker-${dir.name}"
    }
}

include(":sample")

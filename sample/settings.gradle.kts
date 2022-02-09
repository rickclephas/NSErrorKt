pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "sample"

include(":shared")

includeBuild("..") {
    dependencySubstitution {
        substitute(module("com.rickclephas.kmp:nserror-kt")).using(project(":nserror-kt"))
    }
}

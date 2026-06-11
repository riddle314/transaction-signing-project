rootProject.name = "build-logic"
include(":convention")

// This helps the build-logic build itself find plugins
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

// This helps the plugins you write find the Android/Kotlin libraries they need to compile
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
    // This allows your build-logic plugins to also use the same version catalog as the main project
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

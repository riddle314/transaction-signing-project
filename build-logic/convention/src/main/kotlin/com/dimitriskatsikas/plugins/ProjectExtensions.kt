package com.dimitriskatsikas.plugins

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

/**
 * Extension properties for the Project object to easily access versions and libraries from the version catalog.
 */
val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

/**
 * Helper function to retrieve version integers from the "libs" version catalog using an alias.
 */
fun Project.libraryVersion(alias: String): Int {
    val version = libs.findVersion(alias).orElseThrow {
        GradleException("Version alias '$alias' not found in the 'libs' version catalog.")
    }
    return try {
        version.requiredVersion.toInt()
    } catch (e: NumberFormatException) {
        throw GradleException(
            "Version for alias '$alias' is not a valid integer: ${version.requiredVersion}",
            e
        )
    }
}

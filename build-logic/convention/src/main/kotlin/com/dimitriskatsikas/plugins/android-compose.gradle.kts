package com.dimitriskatsikas.plugins

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension

pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

// Enable Compose for whichever Android plugin is applied (app or library)
pluginManager.withPlugin("com.android.application") {
    extensions.configure<ApplicationExtension> {
        buildFeatures {
            compose = true
        }
    }
}

pluginManager.withPlugin("com.android.library") {
    extensions.configure<LibraryExtension> {
        buildFeatures {
            compose = true
        }
    }
}

dependencies {
    val composeBom = libs.findLibrary("androidx.compose.bom").get()

    "implementation"(platform(composeBom))
    "implementation"(libs.findLibrary("androidx.activity.compose").get())
    "implementation"(libs.findLibrary("androidx.compose.ui").get())
    "implementation"(libs.findLibrary("androidx.compose.ui.graphics").get())
    "implementation"(libs.findLibrary("androidx.compose.ui.tooling.preview").get())
    "implementation"(libs.findLibrary("androidx.compose.material3").get())
    "implementation"(libs.findLibrary("androidx.compose.material.icons.extended").get())

    "androidTestImplementation"(platform(composeBom))
    "androidTestImplementation"(libs.findLibrary("androidx.compose.ui.test.junit4").get())
    "debugImplementation"(libs.findLibrary("androidx.compose.ui.tooling").get())
    "debugImplementation"(libs.findLibrary("androidx.compose.ui.test.manifest").get())
}

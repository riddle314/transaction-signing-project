package com.dimitriskatsikas.plugins

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

private const val ANDROID_COMPILE_SDK = "android-compileSdk"
private const val ANDROID_MIN_SDK = "android-minSdk"
private const val ANDROID_TARGET_SDK = "android-targetSdk"
private const val TEST_INSTRUMENTATION_RUNNER = "androidx.test.runner.AndroidJUnitRunner"

// In AGP 9.0+, ApplicationExtension and LibraryExtension do not share a generic CommonExtension interface
// that exposes `defaultConfig`, `compileOptions`, and `testOptions` cleanly.
// Because the DSL was decoupled to avoid generic hell, this minimal duplication is strictly required
// and is the recommended approach for Gradle convention plugins now.
fun Project.configureAndroid(
    extension: ApplicationExtension,
) {
    extension.apply {
        compileSdk = libraryVersion(ANDROID_COMPILE_SDK)

        defaultConfig {
            minSdk = libraryVersion(ANDROID_MIN_SDK)
            testInstrumentationRunner = TEST_INSTRUMENTATION_RUNNER
            // this is application specific
            targetSdk = libraryVersion(ANDROID_TARGET_SDK)
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        testOptions {
            unitTests.all {
                it.useJUnitPlatform()
            }
        }
    }
    configureKotlin()
}

fun Project.configureAndroid(
    extension: LibraryExtension,
) {
    extension.apply {
        compileSdk = libraryVersion(ANDROID_COMPILE_SDK)

        defaultConfig {
            minSdk = libraryVersion(ANDROID_MIN_SDK)
            testInstrumentationRunner = TEST_INSTRUMENTATION_RUNNER
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        testOptions {
            unitTests.all {
                it.useJUnitPlatform()
            }
        }
    }
    configureKotlin()
}

private fun Project.configureKotlin() {
    // This block configures all Kotlin compilation tasks to target JVM 17.
    // We do this at the task level to guarantee uniformity across the project.
    tasks.withType(KotlinCompile::class.java).configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
}

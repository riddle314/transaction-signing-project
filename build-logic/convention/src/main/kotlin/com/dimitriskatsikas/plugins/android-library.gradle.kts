package com.dimitriskatsikas.plugins

import com.android.build.api.dsl.LibraryExtension

pluginManager.apply("com.android.library")

extensions.configure<LibraryExtension> {
    configureAndroid(this)
}

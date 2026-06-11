package com.dimitriskatsikas.plugins

import com.android.build.api.dsl.ApplicationExtension

pluginManager.apply("com.android.application")

extensions.configure<ApplicationExtension> {
    configureAndroid(this)
}

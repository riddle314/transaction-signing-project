package com.dimitriskatsikas.common.previews

import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.res.Configuration.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "Default | x1 font | Light mode",
    device = Devices.DEFAULT,
    fontScale = 1f,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Default | x1 font | Dark mode",
    device = Devices.DEFAULT,
    fontScale = 1f,
    showSystemUi = true,
    uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL,
)
@Preview(
    name = "Small | x2 font",
    device = Devices.PIXEL_2,
    fontScale = 2f,
    showSystemUi = true
)
@Preview(
    name = "Tablet 7 | x1 font",
    device = Devices.NEXUS_7,
    fontScale = 1f,
    showSystemUi = true
)
@Preview(
    name = "Tablet 10 | x1 font",
    device = Devices.PIXEL_TABLET,
    fontScale = 1f,
    showSystemUi = true
)
annotation class Previews

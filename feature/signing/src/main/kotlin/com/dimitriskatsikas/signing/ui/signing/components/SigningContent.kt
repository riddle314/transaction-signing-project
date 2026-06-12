package com.dimitriskatsikas.signing.ui.signing.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dimitriskatsikas.common.previews.Previews
import com.dimitriskatsikas.signing.ui.signing.SigningView
import com.dimitriskatsikas.signing.ui.signing.SigningView.OperationType
import com.dimitriskatsikas.signing.ui.signing.SigningView.SigningMechanism

@Composable
internal fun SigningContent(
    state: SigningView.State,
    onUiAction: (SigningView.UiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

        }
    }
}

@Previews
@Composable
private fun SigningContentDefaultPreview() {
    SigningContent(
        state = SigningView.State.Content(
            operationType = OperationType.WITHDRAWAL,
            signingMechanisms = SigningMechanism.entries
        ),
        onUiAction = {}
    )
}

@Previews
@Composable
private fun SigningContentLoadingPreview() {
    SigningContent(
        state = SigningView.State.Loading,
        onUiAction = {}
    )
}
